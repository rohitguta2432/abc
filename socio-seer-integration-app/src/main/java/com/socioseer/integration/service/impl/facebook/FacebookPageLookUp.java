package com.socioseer.integration.service.impl.facebook;

import java.util.Collections;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.JsonParser;

/**
 * <h3>Facebook Page LookUp</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
public class FacebookPageLookUp implements Runnable {

  private final String PAGE_LOOK_UP_URL = "https://graph.facebook.com/v2.9/%s?access_token=%s&fields=fan_count,name";
  private HttpClient httpClient;
  private SocialHandler socialHandler;
  private SocioSeerProducer<Map<String, Object>> producer;
  private String socialProfileLookupTopic;
  private String pageId;
  private String pageAccessToken;

  /**
   * <h3>Constructor</h3>
   * @param httpClient
   * @param socialhandler
   * @param producer
   * @param socialProfileLookupTopic
   */
  public FacebookPageLookUp(final HttpClient httpClient, final SocialHandler socialhandler,
      final SocioSeerProducer<Map<String, Object>> producer, String socialProfileLookupTopic) {
    this.httpClient = httpClient;
    this.socialHandler = socialhandler;
    this.producer = producer;
    Map<String, String> accessToken = socialhandler.getAccessToken();
    this.pageId = accessToken.get(ModelConstants.PAGE_ID);
    this.pageAccessToken = accessToken.get(ModelConstants.PAGE_ACCESS_TOKEN);
    this.socialProfileLookupTopic = socialProfileLookupTopic;
  }
  
  /**
   * 
   */
  @Override
  public void run() {
    log.info(String.format("Pulling user profile data for facebook handler %s with handler id : %s",
        socialHandler.getHandler(), socialHandler.getId()));
    
    Map<String, Object> response;
    try {
      response = pageLookUp(pageId, pageAccessToken);
      if(!response.isEmpty() && response.get("id")!=null){
        response.put(SocioSeerConstant.KEY_CLIENT_ID, socialHandler.getClientId());
        response.put(SocioSeerConstant.KEY_POST_HANDLER_ID, socialHandler.getHandler());
        response.put(SocioSeerConstant.KEY_PLATFORM, socialHandler.getSocialPlatform().getName());
        producer.produce(socialProfileLookupTopic, PlatformConstant.FACEBOOK_PAGE, response);
      }
    } catch (Exception e) {
      log.error("Error occurred while fetching details for the handler id "+ socialHandler.getHandler());
    }
  }

  /**
   * 
   * @param pageId
   * @param pageAccessToken
   * @return	returns map object
   * @throws Exception
   */
  private Map<String, Object> pageLookUp(String pageId, String pageAccessToken) throws Exception {
    String userLookUpURL = String.format(PAGE_LOOK_UP_URL, pageId, pageAccessToken);
    String response = fetchData(userLookUpURL);
    if (StringUtils.isEmpty(response)) {
      return Collections.emptyMap();
    }
    return parseResponse(response);
  }

  /**
   * <b>Fetching data from facebook</b>	 
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
      log.warn(message);
      throw new SocioSeerException(message);
    }
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

  /**
   * <b>Response Parser</b>
   * @param response
   * @return	returns map object
   * @throws Exception
   */
  private Map<String, Object> parseResponse(String response) throws Exception {
    return JsonParser.getObject(response, new TypeReference<Map<String, Object>>() {});
  }
  
}
