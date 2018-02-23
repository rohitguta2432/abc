package com.socioseer.integration.service.impl.twitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.config.TwitterConfig;
import com.socioseer.integration.service.api.SocialPollerService;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.JsonParser;

import lombok.extern.slf4j.Slf4j;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
public class TwitterPollerServiceImpl implements SocialPollerService {


  private final String FAVORITES_URL = "https://api.twitter.com/1.1/favorites/list.json";
  private final String MENTION_TIME_LINE_URL =
      "https://api.twitter.com/1.1/statuses/mentions_timeline.json";
  private final String USER_TIME_LINE_URL =
      "https://api.twitter.com/1.1/statuses/user_timeline.json";
  private final String ID_STR = "id";

  private final HttpClient httpClient;
  private final SocialHandler socialhandler;
  private final SocioSeerProducer<Map<String, Object>> producer;
  private final String socialPlatformPostTopic;
  private final String socialHandlerTopic;
  private final TwitterConfig twitterConfig;
  private final OAuthConsumer oAuthConsumer;

  /**
   * 
   * @param httpClient
   * @param socialhandler
   * @param producer
   * @param socialPlatformPostTopic
   * @param socialHandlerTopic
   * @param twitterConfig
   */
  public TwitterPollerServiceImpl(final HttpClient httpClient, final SocialHandler socialhandler,
      final SocioSeerProducer<Map<String, Object>> producer, final String socialPlatformPostTopic,
      final String socialHandlerTopic, TwitterConfig twitterConfig) {
    this.httpClient = httpClient;
    this.socialhandler = socialhandler;
    this.producer = producer;
    this.socialPlatformPostTopic = socialPlatformPostTopic;
    this.socialHandlerTopic = socialHandlerTopic;
    this.twitterConfig = twitterConfig;
    Map<String, String> accessToken = socialhandler.getAccessToken();
    this.oAuthConsumer = new CommonsHttpOAuthConsumer(this.twitterConfig.getConsumerKey(),
        this.twitterConfig.getConsumerSecret());
    this.oAuthConsumer.setTokenWithSecret(accessToken.get(ModelConstants.ACCESS_KEY),
        accessToken.get(ModelConstants.ACCESS_SECRET));
  }

  @Override
  public void run() {

    log.info(String.format("Pulling data for twitter handler %s with handler id : %s",
        socialhandler.getHandler(), socialhandler.getId()));

    // fetching user and mention timeline data from twitter
    // List<Map<String, Object>> fetchFavoriteData = fetchFavorites();
    List<Map<String, Object>> mentionTimeLineData = fetchMentionTimeLineData();
    List<Map<String, Object>> userTimeLineData = fetchUsetrTimeLineData();

    // if (!CollectionUtils.isEmpty(fetchFavoriteData)) {
    // enrichClientAndHandlerId(fetchFavoriteData, ModelConstants.FAVORITE_TIMELINE);
    // producer.produce(socialPlatformPostTopic, PlatformConstant.TWITTER, fetchFavoriteData);
    // }

    if (!CollectionUtils.isEmpty(mentionTimeLineData)) {
      enrichClientAndHandlerId(mentionTimeLineData, ModelConstants.MENTION_TIMELINE);
      producer.produce(socialPlatformPostTopic, PlatformConstant.TWITTER, mentionTimeLineData);
    }

    if (!CollectionUtils.isEmpty(userTimeLineData)) {
      enrichClientAndHandlerId(userTimeLineData, ModelConstants.USER_TIMELINE);
      producer.produce(socialPlatformPostTopic, PlatformConstant.TWITTER, userTimeLineData);
    }

    // fetching latest tweet id, to keep track of last tweet fetched
    // String lastFavoriteTweeetId = fetchId(fetchFavoriteData);
    // if (StringUtils.isEmpty(lastFavoriteTweeetId)) {
    // lastFavoriteTweeetId =
    // socialhandler.getAccessToken().get(ModelConstants.LAST_FAVORITE_TWEET_ID);
    // }
    String lastUserTimeLineTweetId = fetchId(userTimeLineData);
    if (StringUtils.isEmpty(lastUserTimeLineTweetId)) {
      lastUserTimeLineTweetId =
          socialhandler.getAccessToken().get(ModelConstants.LAST_USER_TIMELINE_TWEET_ID);
    }
    String lastMentionTimeLineTweetId = fetchId(mentionTimeLineData);
    if (StringUtils.isEmpty(lastMentionTimeLineTweetId)) {
      lastMentionTimeLineTweetId =
          socialhandler.getAccessToken().get(ModelConstants.LAST_FAVORITE_TWEET_ID);
    }


    Map<String, Object> lastTweetIdentifierMap = new HashMap<>();
    // lastTweetIdentifierMap.put(ModelConstants.LAST_FAVORITE_TWEET_ID, lastFavoriteTweeetId);
    lastTweetIdentifierMap.put(ModelConstants.LAST_USER_TIMELINE_TWEET_ID, lastUserTimeLineTweetId);
    lastTweetIdentifierMap.put(ModelConstants.LAST_MENTION_TIMELINE_TWEET_ID,
        lastMentionTimeLineTweetId);
    lastTweetIdentifierMap.put(ModelConstants.CLIENT_ID, socialhandler.getClientId());
    lastTweetIdentifierMap.put(ModelConstants.HANDLER_ID, socialhandler.getId());
    lastTweetIdentifierMap.put(ModelConstants.PLATFORM_TYPE, PlatformConstant.TWITTER);

    producer.produce(socialHandlerTopic, lastTweetIdentifierMap);
  }

  /**
   * Enriching time line data with client and handler id
   * 
   * @param timeLineDataList
   * @param tweetType
   */
  private void enrichClientAndHandlerId(List<Map<String, Object>> timeLineDataList,
      String tweetType) {
    timeLineDataList.forEach(timeLine -> {
      timeLine.put(ModelConstants.CLIENT_ID, socialhandler.getClientId());
      timeLine.put(ModelConstants.HANDLER_ID, socialhandler.getId());
      timeLine.put(ModelConstants.TWEET_TYPE, tweetType);
    });
  }

  /**
   * Fetching last tweet id
   * 
   * @param timeLineData
   * @return returns string
   */
  private String fetchId(List<Map<String, Object>> timeLineData) {
    if (CollectionUtils.isEmpty(timeLineData)) {
      return org.apache.commons.lang3.StringUtils.EMPTY;
    }
    return timeLineData.get(0).get(ID_STR).toString();
  }

  /**
   * Fetching favorite time line data
   * 
   * @return map objects list
   */
  private List<Map<String, Object>> fetchFavorites() {
    String lastfavoriteId =
        socialhandler.getAccessToken().get(ModelConstants.LAST_FAVORITE_TWEET_ID);
    StringBuilder url = new StringBuilder(FAVORITES_URL);
    if (!StringUtils.isEmpty(lastfavoriteId)) {
      url.append("?since_id=").append(lastfavoriteId);
    }
    try {
      String response = fetchData(url.toString());
      return parseResponse(response);
    } catch (Exception e) {
      String message = String.format(
          "Error while fetching favorite data from twitter for client : %s with handler id : %s",
          socialhandler.getClientId(), socialhandler.getId());
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

  /**
   * Fetching user time line data
   * 
   * @return returns map objects list
   */
  private List<Map<String, Object>> fetchUsetrTimeLineData() {
    String lastfavoriteId =
        socialhandler.getAccessToken().get(ModelConstants.LAST_USER_TIMELINE_TWEET_ID);
    StringBuilder url = new StringBuilder(USER_TIME_LINE_URL);
    if (!StringUtils.isEmpty(lastfavoriteId)) {
      url.append("?since_id=").append(lastfavoriteId);
    }
    try {
      String response = fetchData(url.toString());
      return parseResponse(response);
    } catch (Exception e) {
      String message = String.format(
          "Error while fetching user time line data from twitter for client : %s with handler id : %s",
          socialhandler.getClientId(), socialhandler.getId());
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

  /**
   * Fetching mention time line data
   * 
   * @return returns map objects list
   */
  private List<Map<String, Object>> fetchMentionTimeLineData() {
    String lastfavoriteId =
        socialhandler.getAccessToken().get(ModelConstants.LAST_MENTION_TIMELINE_TWEET_ID);
    StringBuilder url = new StringBuilder(MENTION_TIME_LINE_URL);
    if (!StringUtils.isEmpty(lastfavoriteId)) {
      url.append("?since_id=").append(lastfavoriteId);
    }
    try {
      String response = fetchData(url.toString());
      return parseResponse(response);
    } catch (Exception e) {
      String message = String.format(
          "Error while fetching mention time line data from twitter for client : %s with handler id : %s",
          socialhandler.getClientId(), socialhandler.getId());
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

  /**
   * Fetching data from twitter
   * 
   * @param url
   * @return returns string
   * @throws Exception
   */
  private String fetchData(String url) throws Exception {
    HttpGet httpGet = null;
    httpGet = new HttpGet(url);
    oAuthConsumer.sign(httpGet);
    HttpResponse httpResponse = httpClient.execute(httpGet);
    int statusCode = httpResponse.getStatusLine().getStatusCode();
    if (statusCode != HttpStatus.OK.value()) {
      String message = String.format("Twitter responded with code: %s for handler id : %s",
          statusCode, socialhandler.getId());
      log.warn(message);
      throw new SocioSeerException(message);
    }
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

  /**
   * Parsing fetched data from twitter
   * 
   * @param response
   * @return returns map objects list
   * @throws Exception
   */
  private List<Map<String, Object>> parseResponse(String response) throws Exception {
    return JsonParser.getObject(response, new TypeReference<List<Map<String, Object>>>() {});
  }
}
