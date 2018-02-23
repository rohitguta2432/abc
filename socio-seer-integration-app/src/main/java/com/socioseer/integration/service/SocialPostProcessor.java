package com.socioseer.integration.service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.HttpClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.dto.PostScheduleDto;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.cache.Cache;
import com.socioseer.integration.config.TwitterConfig;
import com.socioseer.integration.service.api.SocioSeerPlatformPostService;
import com.socioseer.integration.service.impl.facebook.SocioSeerFacebookPostServiceImpl;
import com.socioseer.integration.service.impl.twitter.SocioSeerTwitterPostServiceImpl;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.BeanUtil;
import com.socioseer.integration.service.util.JsonParser;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Component
public class SocialPostProcessor {

  @Autowired
  private Cache cache;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${kafka.topic.socialPostId}")
  private String topicName;

  @Autowired
  private SocioSeerProducer<Map<String, Object>> producer;

  @Autowired
  private HttpClient httpClient;

  @Autowired
  private RetryTemplate retryTemplate;

  @Autowired
  private TwitterConfig twitterConfig;

  @Autowired
  private BeanUtil beanUtil;

  /**
   * 
   * @param record
   */
  public void process(final ConsumerRecord<String, String> record) {
    PostScheduleDto scheduleDto = parseJson(record.value());
    if (record != null) {
      log.info(String.format(
          "Spawning thread for post received for cliend id: %s and handler id: %s", scheduleDto
              .getPostDto().getClientId(), scheduleDto.getHandlerId()));
      final SocioSeerPlatformPostService<PostScheduleDto> platformService =
          getSocioSeerPlatformService(record.key(), scheduleDto);
      TaskExecutor.execute(platformService);
    }
  }

  /**
   * 
   * @param key
   * @param postScheduleDto
   * @return
   */
  private SocioSeerPlatformPostService<PostScheduleDto> getSocioSeerPlatformService(
      final String key, PostScheduleDto postScheduleDto) {

    SocioSeerPlatformPostService<PostScheduleDto> platformService = null;
    Optional<SocialHandler> socialHandler =
        cache.get(SocialHandler.OBJECT_KEY, postScheduleDto.getHandlerId(), SocialHandler.class);

    if (socialHandler.isPresent()) {
      if (PlatformConstant.FACEBOOK.equalsIgnoreCase(key)) {
        platformService =
            new SocioSeerFacebookPostServiceImpl(beanUtil.getBean(HttpClient.class),
                socialHandler.get(), postScheduleDto, beanUtil.getBean(RetryTemplate.class),
                producer, topicName);
      } else if (PlatformConstant.TWITTER.equalsIgnoreCase(key)) {
        platformService =
            new SocioSeerTwitterPostServiceImpl(beanUtil.getBean(HttpClient.class),
                socialHandler.get(), beanUtil.getBean(RetryTemplate.class), twitterConfig,
                postScheduleDto, producer, topicName);
      }
    } else {
      log.info("No social handler found with id : {0}", postScheduleDto.getHandlerId());
    }
    return platformService;
  }

  /**
   * 
   * @param value
   * @return returns PostScheduleDto
   */
  private PostScheduleDto parseJson(String value) {
    try {
      return JsonParser.getObject(value, PostScheduleDto.class);
    } catch (IOException e) {
      log.error("Error while parsing json.", e);
      throw new SocioSeerException("Error while parsing json.", e);
    }
  }
}
