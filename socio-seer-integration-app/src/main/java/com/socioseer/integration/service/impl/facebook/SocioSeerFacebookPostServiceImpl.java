package com.socioseer.integration.service.impl.facebook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.dto.MediaDto;
import com.socioseer.common.dto.PostDto;
import com.socioseer.common.dto.PostScheduleDto;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.service.api.SocioSeerPlatformPostService;
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
public class SocioSeerFacebookPostServiceImpl implements
    SocioSeerPlatformPostService<PostScheduleDto> {


  private static final String ACCESS_TOKEN = "access_token";
  private static final String MEDIA_TYPE_VIDEO = "VIDEO";
  private static final String MEDIA_TYPE_IMAGE = "IMAGE";
  private static final String FEEDS_URL = "https://graph.facebook.com/v2.9/%s/feed";
  private static final String IMAGE_URL = "https://graph.facebook.com/v2.9/%s/photos";
  private static final String VIDEO_URL = "https://graph.facebook.com/v2.9/%s/videos";
  private static final String ERROR_MESSAGE =
      "Error occurred while posting for campaign id %s and post id %s";

  private final HttpClient httpClient;
  private final SocialHandler socialHandler;
  private final String pageAccessToken;
  private final PostScheduleDto postScheduleDto;
  private final SocioSeerProducer<Map<String, Object>> producer;
  private String topicName;
  private final RetryTemplate retryTemplate;

  /**
   * 
   * @param httpClient
   * @param socialHandler
   * @param postScheduleDto
   * @param retryTemplate
   * @param producer
   * @param topicName
   */
  public SocioSeerFacebookPostServiceImpl(HttpClient httpClient, SocialHandler socialHandler,
      PostScheduleDto postScheduleDto, RetryTemplate retryTemplate,
      SocioSeerProducer<Map<String, Object>> producer, String topicName) {
    this.httpClient = httpClient;
    this.socialHandler = socialHandler;
    this.pageAccessToken =
        this.socialHandler.getAccessToken().get(ModelConstants.PAGE_ACCESS_TOKEN);
    this.postScheduleDto = postScheduleDto;
    this.producer = producer;
    this.topicName = topicName;
    this.retryTemplate = retryTemplate;
  }

  /**
   * 
   */
  @Override
  public void run() {
    PostDto postDto = postScheduleDto.getPostDto();
    try {
      retryTemplate
          .execute(retryContext -> {
            List<MediaDto> mediaList = postDto.getMediaList();
            String status = getStatus(postDto.getText(), mediaList);
            String response = post(status, mediaList);
            Map<String, Object> result = prepareResponse(response, postScheduleDto);
            producer.produce(topicName, result);
            int statusCode = Integer.parseInt(result.get(ModelConstants.STATUS).toString());
            if (statusCode == 200) {
              log.info(String.format(
                  "Facebook post posted for client id : %s with handler id : %s", postScheduleDto
                      .getPostDto().getClientId(), postScheduleDto.getHandlerId()));
            } else {
              log.info(String
                  .format(
                      "Facebook responded with error code : %d while posting data for client id : %s with handler id : %s",
                      statusCode, postScheduleDto.getPostDto().getClientId(),
                      postScheduleDto.getHandlerId()));
            }
            return null;
          });
    } catch (Exception e) {
      String message = String.format(ERROR_MESSAGE, postDto.getCampaignId(), postDto.getId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }


  /**
   * 
   * @param text
   * @param mediaList
   * @return	returns string
   */
  private String getStatus(String text, List<MediaDto> mediaList) {
    StringBuilder status = new StringBuilder();
    if (StringUtils.isNotEmpty(text)) {
      status.append(text);
    }

    if (CollectionUtils.isEmpty(mediaList)) {
      return status.toString();
    }

    String videoURL = null;
    ListIterator<MediaDto> iterator = mediaList.listIterator();
    MediaDto mediaDto = null;
    while (iterator.hasNext()) {
      mediaDto = iterator.next();
      videoURL = mediaDto.getVideoURL();
      if (StringUtils.isNotEmpty(videoURL)) {
        status.append("\n" + videoURL);
        iterator.remove();
      }
    }
    return status.toString();
  }

  /**
   * 
   * @param responseString
   * @param postScheduleDto
   * @return	returns map object
   * @throws Exception
   */
  private Map<String, Object> prepareResponse(String responseString, PostScheduleDto postScheduleDto)
      throws Exception {
    JsonNode responseJSON = JsonParser.getObject(responseString, JsonNode.class);
    Map<String, Object> response = new HashMap<String, Object>();
    int status = 0;
    String message = null;
    if (responseJSON.has("error") && !responseJSON.has("id")) {
      JsonNode jsonNode = responseJSON.get("error");
      JsonNode errorNode = null;
      if (jsonNode.isArray()) {
        errorNode = jsonNode.get(0);
      } else {
        errorNode = jsonNode;
      }
      status = errorNode.get("code").asInt();
      message = errorNode.get("message").asText();
    } else {
      String socialPostId = responseJSON.get("id").asText();
      if (responseJSON.has("post_id")) {
        socialPostId = responseJSON.get("post_id").asText();
      }
      status = 200;
      message = "posted successfully.";
      response.put(SocioSeerConstant.KEY_POST_SOCIAL_ID, socialPostId);
    }
    response.put(ModelConstants.STATUS, status);
    response.put(ModelConstants.MESSAGE, message);
    response.put(SocioSeerConstant.KEY_POST_HANDLER_ID, postScheduleDto.getHandlerId());
    response.put(SocioSeerConstant.KEY_POST_SCHEDULE_ID, postScheduleDto.getId());
    return response;
  }

  /**
   * 
   * @param status
   * @param mediaList
   * @return	returns string
   * @throws Exception
   */
  private String post(String status, List<MediaDto> mediaList) throws Exception {
    String pageId = socialHandler.getAccessToken().get(ModelConstants.PAGE_ID);
    if (CollectionUtils.isEmpty(mediaList)) {
      return feed(status, pageId);
    }

    MediaDto mediaDto = mediaList.get(0);
    String url = StringUtils.EMPTY;
    String mediaType = mediaDto.getMediaType();
    if (mediaType.equals(MEDIA_TYPE_IMAGE)) {
      return uploadImage(pageId, status, mediaDto.getFileLocation());
    }

    if (mediaType.equals(MEDIA_TYPE_VIDEO)) {
      url = String.format(VIDEO_URL, pageId);
      return uploadVideo(pageId, status, mediaDto.getFileLocation());
    }

    if (StringUtils.isEmpty(url)) {
      throw new SocioSeerException("Invalid Media Type" + mediaType + " for the post");
    }
    return StringUtils.EMPTY;
  }

  /**
   * 
   * @param pageId
   * @param status
   * @param fileLocation
   * @return	returns string
   * @throws Exception
   */
  private String uploadVideo(String pageId, String status, String fileLocation) throws Exception {
    HttpPost httpPost = new HttpPost(String.format(VIDEO_URL, pageId));
    CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    File file = new File(fileLocation);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addBinaryBody("source", new FileInputStream(file),
        ContentType.APPLICATION_OCTET_STREAM, file.getName());
    builder.addTextBody("description", status);
    builder.addTextBody(ACCESS_TOKEN, this.pageAccessToken);
    httpPost.setEntity(builder.build());
    HttpResponse httpResponse = httpClient.execute(httpPost);
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

  /**
   * 
   * @param pageId
   * @param status
   * @param fileLocation
   * @return	returns string
   * @throws Exception
   */
  private String uploadImage(String pageId, String status, String fileLocation) throws Exception {
    HttpPost httpPost = new HttpPost(String.format(IMAGE_URL, pageId));
    File file = new File(fileLocation);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addBinaryBody("source", new FileInputStream(file),
        ContentType.APPLICATION_OCTET_STREAM, file.getName());
    builder.addTextBody("caption", status);
    builder.addTextBody(ACCESS_TOKEN, this.pageAccessToken);
    httpPost.setEntity(builder.build());
    HttpResponse httpResponse = httpClient.execute(httpPost);
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

  /**
   * 
   * @param message
   * @param pageId
   * @return	returns string
   * @throws Exception
   */
  private String feed(String message, String pageId) throws Exception {
    HttpPost httpPost = new HttpPost(String.format(FEEDS_URL, pageId));
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    nameValuePairs.add(new BasicNameValuePair("message", message));
    nameValuePairs.add(new BasicNameValuePair(ACCESS_TOKEN, this.pageAccessToken));
    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));;
    HttpResponse httpResponse = httpClient.execute(httpPost);
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

}
