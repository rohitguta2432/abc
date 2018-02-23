package com.socioseer.zuul.filter;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.dto.Response;
import com.socioseer.common.exception.ExpiredAuthenticationTokenException;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.common.exception.UnAuthorizedException;
import com.socioseer.zuul.config.AuthenticationService;
import com.socioseer.zuul.config.AuthorizationService;
import com.socioseer.zuul.exception.BadRequestException;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
public class SocioSeerApiFilter extends ZuulFilter {

  private final List<String> apiList;
  private final AuthorizationService authorizationService;
  private final AuthenticationService authenticationService;
  private final String authHeaderToken;
  private final String secretKey;

  private static final String PRE_FILTER = "pre";
  private static final int FILTER_ORDER = 1;
  private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public SocioSeerApiFilter(List<String> apiList, AuthorizationService authorizationService,
      AuthenticationService authenticationService, String authHeaderToken, String secretKey) {
    this.apiList = apiList;
    this.authorizationService = authorizationService;
    this.authenticationService = authenticationService;
    this.authHeaderToken = authHeaderToken;
    this.secretKey = secretKey;
  }

  /**
   * 
   */
  @Override
  public Object run() {
    try {
      String userId = validateAuthToken(extractAuthTokenFromHeader());
      doUserHaveAccessToResource(userId);
      RequestContext requestContext = RequestContext.getCurrentContext();
      requestContext.getZuulRequestHeaders().put(authHeaderToken, secretKey);
    } catch (Exception e) {
      writeErrorToResponse(e);
      RequestContext.getCurrentContext().clear();
      return null;
    }
    return null;
  }

  @Override
  public boolean shouldFilter() {
    RequestContext requestContext = RequestContext.getCurrentContext();
    HttpServletRequest request = requestContext.getRequest();
    return apiList.stream().anyMatch(api -> request.getRequestURL().toString().contains(api));
  }

  @Override
  public int filterOrder() {
    return FILTER_ORDER;
  }

  @Override
  public String filterType() {
    return PRE_FILTER;
  }

  /**
   * 
   * @param userId
   */
  private void doUserHaveAccessToResource(String userId) {
    RequestContext requestContext = RequestContext.getCurrentContext();
    HttpServletRequest request = requestContext.getRequest();
    String requestedResource = getRequestedResource(request.getRequestURI());
    String httpMethod = request.getMethod();
    ResponseEntity<Response<Boolean>> response =
        authorizationService.haveAccessToResource(createQueryMap(userId, requestedResource,
            httpMethod));
    if (response == null) {
      log.error("No response obtained from authorization API.");
      throw new SocioSeerException("No response obtained from authorization API.");
    }

    if (response.getStatusCode() != HttpStatus.OK) {
      String message =
          String.format("Authorization API responded with error code : %d",
              response.getStatusCodeValue());
      log.error(message);
      throw new SocioSeerException(message);
    }

    if (!response.getBody().getData()) {
      String message =
          String.format("User with id : %s do not have access to requested resource : %s", userId,
              requestedResource);
      log.info(message);
      throw new UnAuthorizedException(message);
    }
  }

  /**
   * 
   * @param userId
   * @param requestedResource
   * @param httpMethod
   * @return returns Map<String, String>
   */ 
  private Map<String, String> createQueryMap(String userId, String requestedResource,
      String httpMethod) {
    Map<String, String> queryParamMap = new HashMap<>();
    queryParamMap.put(SocioSeerConstant.KEY_USER_ID, userId);
    queryParamMap.put(SocioSeerConstant.KEY_HTTP_METHOD, httpMethod);
    queryParamMap.put(SocioSeerConstant.KEY_REQUESTED_RESOURCES, requestedResource);
    return queryParamMap;
  }
/**
 * 
 * @param requestURL
 * @return returns String
 */
  private String getRequestedResource(String requestURL) {

    Optional<String> requestedProtectedAPI =
        apiList.stream().filter(api -> requestURL.contains(api)).findFirst();
    if (requestedProtectedAPI.isPresent()) {
      return requestURL.replace(requestedProtectedAPI.get(), "");
    }
    return requestURL;
  }

  /**
   * Extracting authentication token from header.
   * 
   * @return - Authentication token
   */
  private String extractAuthTokenFromHeader() {
    RequestContext requestContext = RequestContext.getCurrentContext();
    return requestContext.getRequest().getHeader(authHeaderToken);
  }

  /**
   * <p>
   * Method to validate authentication token using Authentication service. The method return a map
   * consisting of validation result and used id only if the validation is successful
   * </p>
   * 
   * @param authToken - the authentication token need to be validated.
   * @return- user id
   */
  private String validateAuthToken(String authToken) {

    if (StringUtils.isEmpty(authToken)) {
      log.error("Auth token not found in header.");
      throw new BadRequestException("Auth token not found in header.");
    }

    Response<Map<String, Object>> response = authenticationService.validateAuthToken(authToken);
    Map<String, Object> data = response.getData();
    if (response == null || CollectionUtils.isEmpty(data)) {
      log.error("Could not obtain response from authentication service.");
      throw new SocioSeerException("Could not obtain response from authentication service.");
    }

    boolean isTokenValid =
        Boolean.valueOf(data.get(SocioSeerConstant.KEY_IS_AUTH_TOKEN_VALID).toString());
    if (!isTokenValid) {
      log.info("Authentication token has expired, please re-generate it.");
      throw new ExpiredAuthenticationTokenException(
          "Authentication token has expired, please re-generate it.");
    }
    return data.get(SocioSeerConstant.KEY_USER_ID).toString();
  }

  /**
   * 
   * @param e
   */
  private void writeErrorToResponse(Exception e) {

    HttpServletResponse httpServletResponse = RequestContext.getCurrentContext().getResponse();
    int statusCode = 0;
    String message = e.getMessage();
    if (e instanceof IllegalArgumentException || e instanceof BadRequestException) {
      statusCode = 400;
    }else if (e instanceof ExpiredAuthenticationTokenException){
      statusCode = 412;
    } else if (e instanceof UnAuthorizedException) {
      statusCode = 403;
    } else if (e instanceof HystrixRuntimeException) {
      statusCode = 503;
      message = "Requested service is unavailable at the moment, please try again.";
    } else {
      statusCode = 500;
    }
    Response<String> response = new Response<String>(statusCode, message, null);
    try (PrintWriter writer = httpServletResponse.getWriter()) {
      writer.write(OBJECT_MAPPER.writeValueAsString(response));
      writer.flush();
    } catch (Exception ex) {
      log.error(
          String.format("Error while writing error message : %s to response", e.getMessage()), e);
    }

  }
}
