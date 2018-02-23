package com.socioseer.integration.service.pubsub.consumer;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.service.api.PlatFormPostService;
import com.socioseer.integration.service.util.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * <h3></h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */

@Slf4j
@Component
public class SocialPlatformConsumer {

  @Autowired
  @Qualifier("platformPostService")
  private PlatFormPostService<Map<String, Object>> platFormPostService;

  @KafkaListener(id = "social-post-consumer", containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.socialPlatformPostTopic}")
  public void postConsumer(ConsumerRecord<String, String> record) {
    processRecord(record);
  }

  /**
   * 
   * @param record
   */
  private void processRecord(ConsumerRecord<String, String> record) {
    if (record != null) {
      Map<String, Object> postData = null;;
      try {
        postData =
            JsonParser.getObject(record.value(), new TypeReference<Map<String, Object>>() {});
        platFormPostService.save(record.key(), postData);
      } catch (Exception e) {
        log.error("Error while persisting post data", e);
        throw new SocioSeerException("Error while persisting post data", e);
      }
    }
  }
}
