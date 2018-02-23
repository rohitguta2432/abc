package com.socioseer.integration.service.impl.twitter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.dto.HashTagDto;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.cache.Cache;
import com.socioseer.integration.config.TwitterConfig;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.JsonParser;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TwitterHashTagsPollerService {

  private final HttpClient httpClient;
  private final SocioSeerProducer<Map<String, Object>> producer;
  private final TwitterConfig twitterConfig;
  private final Cache cache;

  @Value("${kafka.topic.socialPlatformPostTopic}")
  private String socialPlatformPostTopic;

  private static final Map<String, OAuthConsumer> SOCIAL_HANDLER_OAUTH_MAP = new HashMap<>();
  private static final Map<String, Integer> POST_PULL_COUNT_MAP = new HashMap<>();
  private static final List<SocialHandler> SOCIAL_HANDLERS = new ArrayList<>();
  private static int count = 0;
  private final static String SEARCH_URL = "https://api.twitter.com/1.1/search/tweets.json?q=";
  private final static String STATUSES = "statuses";
  private final static String SEARCH_METADATA = "search_metadata";
  private final static String MAX_ID = "max_id";

  /**
   * 
   */
  public void pollHashTags() {
    filterSocialHandler();
    List<HashTagDto> hashTagDtoList = cache.getObjects(Post.POST_HASH_TAG_KEY, HashTagDto.class);
    if (CollectionUtils.isEmpty(hashTagDtoList)) {
      log.info("No hashtags found for tracking");
      return;
    }
    try {
      hashTagDtoList.forEach(hashTagDto -> {
        try {
          pollHashTag(hashTagDto);
        } catch (Exception e) {
          String message =
              String.format("Error while fetching post hashtag data from twitter for post id : %s",
                  hashTagDto.getPostId());
          log.error(message, e);
        }
      });
    } catch (Exception e) {
      log.error("Error while fetching post hashtag data from twitter", e);
    } finally {
      SOCIAL_HANDLERS.clear();
      SOCIAL_HANDLER_OAUTH_MAP.clear();
      POST_PULL_COUNT_MAP.clear();
    }
  }

  /**
   * 
   */
  private void filterSocialHandler() {
    List<SocialHandler> socialHandlers =
        cache.getObjects(SocialHandler.OBJECT_KEY, SocialHandler.class);
    SOCIAL_HANDLERS.clear();
    SOCIAL_HANDLERS.addAll(socialHandlers.stream()
        .filter(sh -> sh.getSocialPlatform().getName().equalsIgnoreCase(PlatformConstant.TWITTER))
        .collect(Collectors.toList()));
  }


  /**
   * 
   * @param hashTagDto
   */
  @SneakyThrows
  private void pollHashTag(HashTagDto hashTagDto) {

    int fetchCount = 0;
    List<Map<String, Object>> statues = null;
    if (!CollectionUtils.isEmpty(hashTagDto.getHashTags())) {
      while (fetchCount < 5) {
        statues = pollhashTagData(hashTagDto);
        if (CollectionUtils.isEmpty(statues)) {
          break;
        }
        fetchCount++;
      }
      cache.put(Post.POST_HASH_TAG_KEY, hashTagDto.getPostId(), hashTagDto);
    }
  }

  /**
   * 
   * @param hashTagDto
   * @return returns map objects list
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> pollhashTagData(HashTagDto hashTagDto) throws Exception {
    if (CollectionUtils.isEmpty(SOCIAL_HANDLERS)) {
      return Collections.emptyList();
    }
    int index = getSocialHandlerIndex();
    SocialHandler socialHandler = SOCIAL_HANDLERS.get(index);
    OAuthConsumer oAuthConsumer = getOAuthConsumer(socialHandler);
    String url = createSearchUrl(hashTagDto);
    HttpGet httpGet = new HttpGet(url);
    oAuthConsumer.sign(httpGet);
    HttpResponse httpResponse = httpClient.execute(httpGet);
    int statusCode = httpResponse.getStatusLine().getStatusCode();
    if (statusCode != HttpStatus.OK.value()) {
      String message = String.format("Twitter responded with code: %s for handler id : %s",
          statusCode, socialHandler.getId());
      log.warn(message);
      throw new SocioSeerException(message);
    }
    String responseAsString = IOUtils.toString(httpResponse.getEntity().getContent());
    Map<String, Object> parsedResponse = parseResponse(responseAsString);

    List<Map<String, Object>> statuses = (List<Map<String, Object>>) parsedResponse.get(STATUSES);
    Map<String, Object> searhMetaData = (Map<String, Object>) parsedResponse.get(SEARCH_METADATA);
    String maxId = searhMetaData.get(MAX_ID).toString();
    hashTagDto.setMaxId(maxId);
    if (!CollectionUtils.isEmpty(statuses)) {
      producer.produce(socialPlatformPostTopic, PlatformConstant.TWITTER_HASHTAG_COLLECTION,
          enrichData(statuses, hashTagDto));
    }
    return statuses;
  }

  /**
   * 
   * @param statuses
   * @param hashTagDto
   * @return returns returns map objects list
   */
  private List<Map<String, Object>> enrichData(List<Map<String, Object>> statuses,
      HashTagDto hashTagDto) {
    statuses.stream().forEach(status -> {
      status.put(ModelConstants.POST_ID, hashTagDto.getPostId());
      status.put(ModelConstants.CLIENT_ID, hashTagDto.getClientId());
      if (!StringUtils.isEmpty(hashTagDto.getCampaignId())) {
        status.put(ModelConstants.CAMPAIGN_ID, hashTagDto.getCampaignId());
      }
      status.put(ModelConstants.POST_HASHTAGS, hashTagDto.getHashTags());
    });
    return statuses;
  }

  private int getSocialHandlerIndex() {
    if (count >= SOCIAL_HANDLERS.size()) {
      count = 0;
    }
    return count++;
  }

  /**
   * 
   * @param hashTagDto
   * @return returns string
   * @throws UnsupportedEncodingException
   */
  private String createSearchUrl(HashTagDto hashTagDto) throws UnsupportedEncodingException {
    StringBuilder url = new StringBuilder(SEARCH_URL);
    if (!CollectionUtils.isEmpty(hashTagDto.getHashTags())) {
      String hashTags = String.join("+", hashTagDto.getHashTags());
      url.append(URLEncoder.encode(hashTags, "UTF-8"));
    }
    url.append("&count=100");
    if (!StringUtils.isEmpty(hashTagDto.getMaxId())) {
      url.append("&max_id=");
      url.append(hashTagDto.getMaxId());
    }
    return url.toString();
  }

  /**
   * 
   * @param socialHandler
   * @return returns OAuthConsumer
   */
  private OAuthConsumer getOAuthConsumer(SocialHandler socialHandler) {
    OAuthConsumer oAuthConsumer = SOCIAL_HANDLER_OAUTH_MAP.get(socialHandler.getId());
    if (oAuthConsumer == null) {
      Map<String, String> accessToken = socialHandler.getAccessToken();
      oAuthConsumer = new CommonsHttpOAuthConsumer(twitterConfig.getConsumerKey(),
          twitterConfig.getConsumerSecret());
      oAuthConsumer.setTokenWithSecret(accessToken.get(ModelConstants.ACCESS_KEY),
          accessToken.get(ModelConstants.ACCESS_SECRET));
      SOCIAL_HANDLER_OAUTH_MAP.put(socialHandler.getId(), oAuthConsumer);
    }
    return oAuthConsumer;
  }

  /**
   * Parsing fetched data from twitter
   * 
   * @param response
   * @return returns map object
   * @throws Exception
   */
  private Map<String, Object> parseResponse(String response) throws Exception {
    return JsonParser.getObject(response, new TypeReference<Map<String, Object>>() {});
  }

}
