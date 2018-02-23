package com.socioseer.integration.service.impl;

import java.util.Collections;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.config.TwitterConfig;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.JsonParser;

/**
 * <h3>TwitterUserProfile LookUp</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
public class TwitterUserProfileLookUp implements Runnable {

  private final String USER_LOOK_UP_URL = "https://api.twitter.com/1.1/users/show.json?screen_name=%s";
  private HttpClient httpClient;
  private SocialHandler socialHandler;
  private SocioSeerProducer<Map<String, Object>> producer;
  private String socialProfileLookupTopic;
  private final OAuthConsumer oAuthConsumer;

  /**
   * <b>Constructor</b>
   * @param httpClient
   * @param socialhandler
   * @param producer
   * @param twitterConfig
   * @param socialProfileLookupTopic
   */
  public TwitterUserProfileLookUp(final HttpClient httpClient, final SocialHandler socialhandler,
      final SocioSeerProducer<Map<String, Object>> producer, TwitterConfig twitterConfig, String socialProfileLookupTopic) {
    this.httpClient = httpClient;
    this.socialHandler = socialhandler;
    this.producer = producer;
    Map<String, String> accessToken = socialhandler.getAccessToken();
    this.oAuthConsumer =
        new CommonsHttpOAuthConsumer(twitterConfig.getConsumerKey(),
            twitterConfig.getConsumerSecret());
    this.oAuthConsumer.setTokenWithSecret(accessToken.get(ModelConstants.ACCESS_KEY),
        accessToken.get(ModelConstants.ACCESS_SECRET));
    this.socialProfileLookupTopic = socialProfileLookupTopic;
  }

  @Override
  public void run() {
    log.info(String.format("Pulling user profile data for twitter handler %s with handler id : %s",
        socialHandler.getHandler(), socialHandler.getId()));
    
    Map<String, Object> response;
    try {
    
    	Map<String, String> accessToken = socialHandler.getAccessToken(); 
    	
      response = userLookUp(accessToken.get("screen_name"));
      if(!response.isEmpty() && response.get("id")!=null){
        response.put(SocioSeerConstant.KEY_CLIENT_ID, socialHandler.getClientId());
        response.put(SocioSeerConstant.KEY_POST_HANDLER_ID, socialHandler.getHandler());
        response.put(SocioSeerConstant.KEY_PLATFORM, socialHandler.getSocialPlatform().getName());
        producer.produce(socialProfileLookupTopic, PlatformConstant.TWITTER_USER, response);
      }
    } catch (Exception e) {
      log.error("Error occurred while fetching details for the handler id "+ socialHandler.getHandler());
    }
  }

  /**
   * <b>User LookUp</b>
   * @param handler
   * @return   returns map object
   * @throws Exception
   */
  private Map<String, Object> userLookUp(String handler) throws Exception {
    String userLookUpURL = String.format(USER_LOOK_UP_URL, handler);
    String response = fetchData(userLookUpURL);
    if (StringUtils.isEmpty(response)) {
      return Collections.emptyMap();
    }
    return parseResponse(response);
  }

  /**
   * <b>Fetching data from twitter</b>
   * @param url
   * @return	returns string
   * @throws Exception
   */
  private String fetchData(String url) throws Exception {
    HttpGet httpGet = null;
    httpGet = new HttpGet(url);
    oAuthConsumer.sign(httpGet);
    HttpResponse httpResponse = httpClient.execute(httpGet);
    int statusCode = httpResponse.getStatusLine().getStatusCode();
    if (statusCode != HttpStatus.OK.value()) {
      String message =
          String.format("Twitter responded with code: %s for handler id : %s", statusCode,
              socialHandler.getId());
      log.warn(message);
      throw new SocioSeerException(message);
    }
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

  /**
   * 
   * @param response
   * @return	returns map object
   * @throws Exception
   */
  private Map<String, Object> parseResponse(String response) throws Exception {
	  
    return JsonParser.getObject(response, new TypeReference<Map<String, Object>>() {});
  }

}
