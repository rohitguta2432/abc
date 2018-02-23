package com.socioseer.integration.service.impl.twitter;

import java.util.List;
import java.util.Map;

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
import com.socioseer.integration.config.TwitterConfig;
import com.socioseer.integration.service.api.SocialPollerService;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.JsonParser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

@Slf4j
public class TwitterPostPollerService implements SocialPollerService {

  private static final String POST_URL = "https://api.twitter.com/1.1/statuses/show/%s.json";

  private final HttpClient httpClient;
  private final SocialHandler socialhandler;
  private final SocioSeerProducer<Map<String, Object>> producer;
  private final String socialPlatformPostTopic;
  private final TwitterConfig twitterConfig;
  private final OAuthConsumer oAuthConsumer;
  private final List<FBPostDto> fbPostDtos;

  public TwitterPostPollerService(HttpClient httpClient, SocialHandler socialhandler,
      SocioSeerProducer<Map<String, Object>> producer, String socialPlatformPostTopic,
      TwitterConfig twitterConfig, List<FBPostDto> fbPostDtos) {
    super();
    this.httpClient = httpClient;
    this.socialhandler = socialhandler;
    this.producer = producer;
    this.socialPlatformPostTopic = socialPlatformPostTopic;
    this.twitterConfig = twitterConfig;
    Map<String, String> accessToken = socialhandler.getAccessToken();
    this.oAuthConsumer = new CommonsHttpOAuthConsumer(this.twitterConfig.getConsumerKey(),
        this.twitterConfig.getConsumerSecret());
    this.oAuthConsumer.setTokenWithSecret(accessToken.get(ModelConstants.ACCESS_KEY),
        accessToken.get(ModelConstants.ACCESS_SECRET));
    this.fbPostDtos = fbPostDtos;
  }

  @Override
  @SneakyThrows
  public void run() {

    if (CollectionUtils.isEmpty(fbPostDtos)) {
      log.info("No post to pull.");
      return;
    }

    fbPostDtos.parallelStream().forEach(post -> {
      try {
        String fetchData = fetchData(String.format(POST_URL, post.getSocialPostId()));
        Map<String, Object> parsedResponse = parseResponse(fetchData);
        enrichClientAndHandlerId(parsedResponse, ModelConstants.TWITTER_POSTS);
        producer.produce(socialPlatformPostTopic, PlatformConstant.TWITTER_POST_COLLECTION,
            parsedResponse);
      } catch (Exception e) {
        String message = String.format("Error while fetching post details for post id : %s",
            post.getSocialPostId());
        log.error(message, e);
      }
    });

  }

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

  private void enrichClientAndHandlerId(Map<String, Object> postData, String tweetType) {
    postData.put(ModelConstants.CLIENT_ID, socialhandler.getClientId());
    postData.put(ModelConstants.HANDLER_ID, socialhandler.getId());
    postData.put(ModelConstants.TWEET_TYPE, tweetType);
  }

  private Map<String, Object> parseResponse(String response) throws Exception {
    return JsonParser.getObject(response, new TypeReference<Map<String, Object>>() {});
  }

}
