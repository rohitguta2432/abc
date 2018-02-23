package com.socioseer.integration.service.impl.facebook;

import java.net.URLEncoder;
import java.time.Instant;
import java.util.HashMap;
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
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.service.api.SocialPollerService;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.JsonParser;

/**
 * <h3>FacebookPagePoller Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
public class FBPagePollerServiceImpl implements SocialPollerService {

  private static final String PAGE_TAGGED_URL =
      "https://graph.facebook.com/v2.9/%s/tagged?access_token=%s&limit=100";

  private final HttpClient httpClient;
  private final SocialHandler socialhandler;
  private final SocioSeerProducer<Map<String, Object>> producer;
  private final String socialPlatformPostTopic;
  private final String socialHandlerTopic;
  private final String fieldNames;


  /**
   * <b>Constructor</b>
   * @param httpClient
   * @param socialhandler
   * @param producer
   * @param socialPlatformPostTopic
   * @param socialHandlerTopic
   * @param fieldNames
   */
  public FBPagePollerServiceImpl(HttpClient httpClient, SocialHandler socialhandler,
      SocioSeerProducer<Map<String, Object>> producer, String socialPlatformPostTopic,
      String socialHandlerTopic, String fieldNames) {
    this.httpClient = httpClient;
    this.socialhandler = socialhandler;
    this.producer = producer;
    this.socialPlatformPostTopic = socialPlatformPostTopic;
    this.socialHandlerTopic = socialHandlerTopic;
    this.fieldNames = fieldNames;
  }


  /**
   * 
   */
  @Override
  @SneakyThrows
  public void run() {

    String pageId = socialhandler.getAccessToken().get(ModelConstants.PAGE_ID).toString();
    String accessToken = socialhandler.getAccessToken().get(ModelConstants.PAGE_ACCESS_TOKEN).toString();

    if (StringUtils.isEmpty(pageId) || StringUtils.isEmpty(accessToken)) {
      log.warn(String.format(
          "Cannot pull data for client : %s handler : %s with empty pageId or accessToken",
          socialhandler.getClientId(), socialhandler.getId()));
      return;
    }

    try {
      long until = Instant.now().getEpochSecond();
      List<Map<String, Object>> pageMentions = fetchPageMentions(pageId, accessToken, until);

      if (!CollectionUtils.isEmpty(pageMentions)) {
        enrichClientAndHandlerId(pageMentions, ModelConstants.FB_TYPE_TAGGED);
        producer.produce(socialPlatformPostTopic, PlatformConstant.FACEBOOK, pageMentions);
      }

      Map<String, Object> lastPolledTime = new HashMap<>();
      lastPolledTime.put(ModelConstants.FB_TAGGED_SINCE, until);
      lastPolledTime.put(ModelConstants.CLIENT_ID, socialhandler.getClientId());
      lastPolledTime.put(ModelConstants.HANDLER_ID, socialhandler.getId());
      lastPolledTime.put(ModelConstants.PLATFORM_TYPE, PlatformConstant.FACEBOOK);
      producer.produce(socialHandlerTopic, lastPolledTime);
    } catch (Exception e) {
      String message =
          String.format("Error while fetching FB post for client : %s with page id : %s",
              socialhandler.getClientId(), pageId);
      log.error(message, e);
    }
  }

  private void enrichClientAndHandlerId(List<Map<String, Object>> timeLineDataList, String postType) {
    timeLineDataList.forEach(timeLine -> {
      timeLine.put(ModelConstants.CLIENT_ID, socialhandler.getClientId());
      timeLine.put(ModelConstants.HANDLER_ID, socialhandler.getId());
      timeLine.put(ModelConstants.FB_TYPE, postType);
    });
  }

  /**
   * <b>Fetch page mentions</b>
   * @param pageId
   * @param accessToken
   * @param until
   * @return	returns map objects list
   */
  @SneakyThrows
  private List<Map<String, Object>> fetchPageMentions(String pageId, String accessToken, long until) {
    StringBuilder url = new StringBuilder(String.format(PAGE_TAGGED_URL, pageId, accessToken));
    url.append("&fields=");
    url.append(URLEncoder.encode(fieldNames, "UTF-8"));
    String sinceAsString = socialhandler.getAccessToken().get(ModelConstants.FB_POSTS_SINCE);
    if (!StringUtils.isEmpty(sinceAsString)) {
      long since = Long.valueOf(sinceAsString);
      url.append("&since=");
      url.append(since);
      url.append("&until=");
      url.append(until);
    }
    String responseAsString = fetchData(url.toString());
    try {
      return parseResponse(responseAsString);
    } catch (Exception e) {
      String message =
          String
              .format(
                  "Error while fetching page posts data from facebook for client : %s with handler id : %s",
                  socialhandler.getClientId(), socialhandler.getId());
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
              socialhandler.getId());
      log.error(message);
      throw new SocioSeerException(message);
    }
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

  /**
   * 
   * @param responseAsString
   * @return	returns map object list
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> parseResponse(String responseAsString) throws Exception {
    Map<String, Object> response =
        JsonParser.getObject(responseAsString, new TypeReference<Map<String, Object>>() {});
    return (List<Map<String, Object>>) response.get("data");
  }

}
