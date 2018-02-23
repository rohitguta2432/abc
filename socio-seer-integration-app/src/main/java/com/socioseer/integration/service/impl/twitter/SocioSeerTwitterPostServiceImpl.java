package com.socioseer.integration.service.impl.twitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.campaign.enums.MediaType;
import com.socioseer.common.dto.MediaDto;
import com.socioseer.common.dto.PostDto;
import com.socioseer.common.dto.PostScheduleDto;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.config.TwitterConfig;
import com.socioseer.integration.service.api.SocioSeerPlatformPostService;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.JsonParser;
import com.socioseer.integration.service.util.MediaUtil;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
public class SocioSeerTwitterPostServiceImpl implements
    SocioSeerPlatformPostService<PostScheduleDto> {

  private static final String PARAM_TOTAL_BYTES = "total_bytes";
  private static final String PARAM_MEDIA_TYPE = "media_type";
  private static final String PARAM_SEGMENT_INDEX = "segment_index";
  private static final String PARAM_COMMAND = "command";
  private static final String KEY_MEDIA_ID = "media_id";
  private static final String PARAM_MEDIA = "media";
  private static final String PARAM_MEDIA_IDS = "media_ids";
  private static final String PARAM_STATUS = "status";
  private static final String UPLOAD_MEDIA_URL = "https://upload.twitter.com/1.1/media/upload.json";
  private static final String UPLOAD_STATUS = "https://api.twitter.com/1.1/statuses/update.json";
  private static final String ERROR_MESSAGE =
      "Error occurred while tweeting for campaign id %s and post id %s";

  private final HttpClient httpClient;
  private final OAuthConsumer oAuthConsumer;
  private final SocialHandler socialHandler;
  private final RetryTemplate retryTemplate;
  private final TwitterConfig twitterConfig;
  private final PostScheduleDto postScheduleDto;
  private final SocioSeerProducer<Map<String, Object>> producer;
  private String topicName;

  /**
   * 
   * @param httpClient
   * @param socialHandler
   * @param retryTemplate
   * @param twitterConfig
   * @param postScheduleDto
   * @param producer
   * @param topicName
   */
  public SocioSeerTwitterPostServiceImpl(HttpClient httpClient, SocialHandler socialHandler,
      RetryTemplate retryTemplate, TwitterConfig twitterConfig, PostScheduleDto postScheduleDto,
      SocioSeerProducer<Map<String, Object>> producer, String topicName) {
    this.httpClient = httpClient;
    this.socialHandler = socialHandler;
    this.twitterConfig = twitterConfig;
    Map<String, String> accessToken = this.socialHandler.getAccessToken();
    this.oAuthConsumer =
        new CommonsHttpOAuthConsumer(this.twitterConfig.getConsumerKey(),
            this.twitterConfig.getConsumerSecret());
    this.oAuthConsumer.setTokenWithSecret(accessToken.get(ModelConstants.ACCESS_KEY),
        accessToken.get(ModelConstants.ACCESS_SECRET));
    this.retryTemplate = retryTemplate;
    this.postScheduleDto = postScheduleDto;
    this.producer = producer;
    this.topicName = topicName;
  }

  /**
   * 
   */
  @Override
  public void run() {
    log.info(String.format("Scheduled post received for cliend id: %s and handler id: %s",
        postScheduleDto.getPostDto().getClientId(), postScheduleDto.getHandlerId()));
    PostDto postDto = postScheduleDto.getPostDto();
    try {
      String status = getStatus(postDto.getText(), postDto.getMediaList());
      List<String> splitText = splitText(status + "");
      retryTemplate.execute(retryContext -> {
        String text = "";
        String tweetMediaIds = null;
        String tweetStatus = null;
        for (int i = 0; i < splitText.size(); i++) {
          text = splitText.get(i);
          if (i == 0) {
            tweetMediaIds = getTwitterUploadedMediaIds(postDto.getMediaList());
            tweetStatus = tweet(text, tweetMediaIds);
          } else {
            tweetStatus = tweet(text, null);
          }
          Map<String, Object> response = prepareResponse(tweetStatus, postScheduleDto);
          producer.produce(topicName, response);
          int statusCode = Integer.parseInt(response.get(ModelConstants.STATUS).toString());
          if (statusCode != 200) {
            break;
          }
          log.info(String.format("Tweet posted for client id : %s with handler id : %s",
              postScheduleDto.getPostDto().getClientId(), postScheduleDto.getHandlerId()));
        }
        return null;
      });
    } catch (Exception e) {
      String message = String.format(ERROR_MESSAGE, postDto.getCampaignId(), postDto.getId());
      log.error(message, e);
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
    for (MediaDto mediaDto : mediaList) {
      videoURL = mediaDto.getVideoURL();
      if (StringUtils.isNotEmpty(videoURL)) {
        status.append("\n" + videoURL);
      }
    }
    return status.toString();
  }

  /**
   * 
   * @param mediaList
   * @return	returns string
   * @throws Exception
   */
  private String getTwitterUploadedMediaIds(List<MediaDto> mediaList) throws Exception {
    String tweetMediaIds = StringUtils.EMPTY;
    if (!CollectionUtils.isEmpty(mediaList)) {
      tweetMediaIds = getMediaSocialIds(mediaList);
    }
    return tweetMediaIds;
  }

  /**
   * 
   * @param status
   * @param twitterUploadedMediaIds
   * @return	returns string
   * @throws Exception
   */
  private String tweet(String status, String twitterUploadedMediaIds) throws Exception {
    HttpPost httpPost = null;
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    nameValuePairs.add(new BasicNameValuePair(PARAM_STATUS, status));
    httpPost = new HttpPost(UPLOAD_STATUS);
    if (StringUtils.isNotEmpty(twitterUploadedMediaIds)) {
      nameValuePairs.add(new BasicNameValuePair(PARAM_MEDIA_IDS, twitterUploadedMediaIds));
    }
    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    oAuthConsumer.sign(httpPost);
    HttpResponse httpResponse = httpClient.execute(httpPost);
    httpPost.completed();
    return IOUtils.toString(httpResponse.getEntity().getContent());
  }

  /**
   * 
   * @param mediaList
   * @return	returns string
   * @throws Exception
   */
  private String getMediaSocialIds(List<MediaDto> mediaList) throws Exception {
    List<String> mediaFiles =
        mediaList.stream().map(mediaDto -> mediaDto.getFileLocation()).collect(Collectors.toList());
    List<String> mediaUploadedIds = new ArrayList<String>();
    File originalFile = null;
    String mediaId = null;
    HttpPost httpPost = null;
    for (String mediaFile : mediaFiles) {
      originalFile = new File(mediaFile);
      httpPost = new HttpPost(UPLOAD_MEDIA_URL);
      oAuthConsumer.sign(httpPost);
      if (MediaUtil.getMediaType(originalFile.getName()).equals(MediaType.VIDEO)) {
        mediaId = uploadVideoInit(originalFile, httpPost);
        uploadVideoAppend(originalFile, httpPost, mediaId);
        uploadVideoFinalize(originalFile, httpPost, mediaId);
      } else {
        mediaId = uploadImage(originalFile, httpPost);
      }
      mediaUploadedIds.add(mediaId);
    }

    if (!CollectionUtils.isEmpty(mediaUploadedIds)) {
      return StringUtils.join(mediaUploadedIds, ',');
    }
    return StringUtils.EMPTY;
  }

  /**
   * 
   * @param originalFile
   * @param httpPost
   * @return	returns string
   * @throws Exception
   */
  private String uploadVideoInit(File originalFile, HttpPost httpPost) throws Exception {
    String mediaId;
    String responseEntity;
    HttpResponse response;
    JsonNode mediaResponse;
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody(PARAM_COMMAND, "INIT");
    builder.addTextBody(PARAM_MEDIA_TYPE, "video/mp4");
    builder.addTextBody(PARAM_TOTAL_BYTES, "" + originalFile.length());
    HttpEntity multipart = builder.build();
    oAuthConsumer.sign(httpPost);
    httpPost.setEntity(multipart);
    response = httpClient.execute(httpPost);
    responseEntity = IOUtils.toString(response.getEntity().getContent());
    mediaResponse = JsonParser.toObject(responseEntity, JsonNode.class);
    mediaId = mediaResponse.get(KEY_MEDIA_ID).asText();
    return mediaId;
  }

  /**
   * 
   * @param originalFile
   * @param httpPost
   * @param mediaId
   * @throws Exception
   */
  private void uploadVideoAppend(File originalFile, HttpPost httpPost, String mediaId)
      throws Exception {
    HttpResponse response;
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody(PARAM_COMMAND, "APPEND");
    builder.addTextBody(KEY_MEDIA_ID, mediaId);
    builder.addTextBody(PARAM_SEGMENT_INDEX, "0");
    builder.addBinaryBody(PARAM_MEDIA, new FileInputStream(originalFile),
        ContentType.APPLICATION_OCTET_STREAM, originalFile.getName());
    HttpEntity multipart = builder.build();
    oAuthConsumer.sign(httpPost);
    httpPost.setEntity(multipart);
    response = httpClient.execute(httpPost);
    int statusCode = response.getStatusLine().getStatusCode();
    if (statusCode <= 200 || statusCode >= 300) {
      throw new SocioSeerException("Media Upload Fail");
    }
  }

  /**
   * 
   * @param originalFile
   * @param httpPost
   * @param mediaId
   * @return	returns string
   * @throws Exception
   */
  private String uploadVideoFinalize(File originalFile, HttpPost httpPost, String mediaId)
      throws Exception {
    String responseEntity;
    HttpResponse response;
    JsonNode mediaResponse;
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addTextBody(PARAM_COMMAND, "FINALIZE");
    builder.addTextBody(KEY_MEDIA_ID, mediaId);
    oAuthConsumer.sign(httpPost);
    httpPost.setEntity(builder.build());
    response = httpClient.execute(httpPost);
    responseEntity = IOUtils.toString(response.getEntity().getContent());
    mediaResponse = JsonParser.toObject(responseEntity, JsonNode.class);
    mediaId = mediaResponse.get(KEY_MEDIA_ID).asText();
    if (StringUtils.isBlank(mediaId)) {
      log.error("media cannot be uploaded to twitter, response " + responseEntity);
      throw new SocioSeerException("Media cannot be uploaded");
    }
    return mediaId;
  }

  /**
   * 
   * @param originalFile
   * @param httpPost
   * @return	returns string
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ClientProtocolException
   */
  private String uploadImage(File originalFile, HttpPost httpPost) throws FileNotFoundException,
      IOException, ClientProtocolException {
    String mediaId;
    String responseEntity;
    HttpResponse response;
    JsonNode mediaResponse;
    httpPost.setEntity(multipartBuilder(originalFile));
    response = httpClient.execute(httpPost);
    responseEntity = IOUtils.toString(response.getEntity().getContent());
    mediaResponse = JsonParser.toObject(responseEntity, JsonNode.class);
    mediaId = mediaResponse.get(KEY_MEDIA_ID).asText();
    if (StringUtils.isBlank(mediaId)) {
      log.error("media cannot be uploaded to twitter, response " + responseEntity);
      throw new SocioSeerException("Media cannot be uploaded");
    }
    return mediaId;
  }

  /**
   * 
   * @param originalFile
   * @return	returns HttpEntity
   * @throws FileNotFoundException
   */
  private HttpEntity multipartBuilder(File originalFile) throws FileNotFoundException {
    HttpEntity multipart;
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addBinaryBody(PARAM_MEDIA, new FileInputStream(originalFile),
        ContentType.APPLICATION_OCTET_STREAM, originalFile.getName());
    multipart = builder.build();
    return multipart;
  }

  /**
   * 
   * @param responseString
   * @param postScheduleDto
   * @return	HttpEntity
   * @throws Exception
   */
  private Map<String, Object> prepareResponse(String responseString, PostScheduleDto postScheduleDto)
      throws Exception {
    JsonNode responseJSON = JsonParser.getObject(responseString, JsonNode.class);
    Map<String, Object> response = new HashMap<String, Object>();
    int status = 0;
    String message = null;
    if (responseJSON.has("errors") && !responseJSON.has("id")) {
      JsonNode jsonNode = responseJSON.get("errors");
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
      status = 200;
      message = "Tweet posted successfully.";
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
   * @param text
   * @return	returns string list
   */
  private List<String> splitText(String text) {
    List<String> splitStrings = new ArrayList<String>();
    text = text.trim();
    String[] words = text.split(" ");
    StringBuilder sentence = new StringBuilder();
    if (text.length() <= 140) {
      splitStrings.add(text);
    } else {
      int count = 0;
      sentence.append((++count) + "/%s ");
      for (int i = 0; i < words.length; i++) {
        sentence.append(words[i]);
        if (sentence.length() >= 140) {
          String split = sentence.substring(0, sentence.lastIndexOf(" "));
          splitStrings.add(split);
          sentence = new StringBuilder();
          sentence.append((++count) + "/%s ");
          i--;
        } else {
          sentence.append(" ");
        }
      }
      if (sentence.length() > 0)
        splitStrings.add(sentence.toString());
    }
    return splitStrings.stream().map(string -> String.format(string, splitStrings.size()))
        .collect(Collectors.toList());
  }

}
