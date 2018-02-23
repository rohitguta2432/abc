package com.socioseer.integration.service.impl.facebook;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.dto.FBPostDto;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.service.api.SocialPollerService;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.JsonParser;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
public class FBPostPollerService implements SocialPollerService {

  private static final String PAGE_POST_URL = "https://graph.facebook.com/v2.9/%s?access_token=%s";

  private final HttpClient httpClient;
  private final SocialHandler socialHandler;
  private final SocioSeerProducer<Map<String, Object>> producer;
  private final String socialPlatformPostTopic;
  private final String fieldNames;
  private final List<FBPostDto> fbPostDtos;

  /**
   * 
   * @param httpClient
   * @param socialHandler
   * @param producer
   * @param socialPlatformPostTopic
   * @param fieldNames
   * @param fbPostDtos
   */
  public FBPostPollerService(HttpClient httpClient, SocialHandler socialHandler,
      SocioSeerProducer<Map<String, Object>> producer, String socialPlatformPostTopic,
      String fieldNames, List<FBPostDto> fbPostDtos) {
    this.httpClient = httpClient;
    this.socialHandler = socialHandler;
    this.producer = producer;
    this.socialPlatformPostTopic = socialPlatformPostTopic;
    this.fieldNames = fieldNames;
    this.fbPostDtos = fbPostDtos;
  }

  /**
   * 
   */
  @Override
  public void run() {

    if (CollectionUtils.isEmpty(fbPostDtos)) {
      return;
    }

    fbPostDtos.stream()
        .forEach(
            fbPostDto -> {
              try {
                final String accessToken =
                    socialHandler.getAccessToken().get(ModelConstants.PAGE_ACCESS_TOKEN);
                Map<String, Object> postData =
                    fetchPostStats(fbPostDto.getSocialPostId(), accessToken);
                enrichClientAndHandlerId(postData, ModelConstants.FB_TYPE_POST);
                producer.produce(socialPlatformPostTopic, PlatformConstant.FACEBOOK, postData);
              } catch (Exception e) {
                log.error(e.getMessage(), e);
              }
            });
  }

  /**
   * 
   * @param postData
   * @param postType
   */
  private void enrichClientAndHandlerId(Map<String, Object> postData, String postType) {
    postData.put(ModelConstants.CLIENT_ID, socialHandler.getClientId());
    postData.put(ModelConstants.HANDLER_ID, socialHandler.getId());
    postData.put(ModelConstants.FB_TYPE, postType);
  }

  /**
   * 
   * @param postId
   * @param accessToken
   * @return	returns map object
   */
  @SneakyThrows
  private Map<String, Object> fetchPostStats(String postId, String accessToken) {
    StringBuilder url = new StringBuilder(String.format(PAGE_POST_URL, postId, accessToken));
    url.append("&fields=");
    url.append(URLEncoder.encode(fieldNames, "UTF-8"));
    String responseAsString = fetchData(url.toString());
    try {
      return parseResponse(responseAsString);
    } catch (Exception e) {
      String message =
          String
              .format(
                  "Error while fetching post data (%s) from facebook for client (%s) with handler id (%s)",
                  postId, socialHandler.getClientId(), socialHandler.getId());
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }

  }

  /**
   * 
   * @param url
   * @return	returns string
   * @throws Exception
   */
  private String fetchData(String url) throws Exception {
    HttpGet httpGet = null;
    httpGet = new HttpGet(url);
    HttpResponse httpResponse = httpClient.execute(httpGet);
    int statusCode = httpResponse.getStatusLine().getStatusCode();
    if (statusCode != HttpStatus.OK.value()) {
      String message =
          String.format("Facebook responded with code: %s for handler id : %s", statusCode,
              socialHandler.getId());
      log.error(message);
      throw new SocioSeerException(message);
    }
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

  /**
   * 
   * @param responseAsString
   * @return	returns map object
   * @throws Exception
   */
  private Map<String, Object> parseResponse(String responseAsString) throws Exception {
    return JsonParser.getObject(responseAsString, new TypeReference<Map<String, Object>>() {});
  }

}
