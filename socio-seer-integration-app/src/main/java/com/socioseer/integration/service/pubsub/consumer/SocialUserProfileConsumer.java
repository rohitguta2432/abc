package com.socioseer.integration.service.pubsub.consumer;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.service.api.PlatFormPostService;
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
@Component
public class SocialUserProfileConsumer {

  @Autowired
  @Qualifier("platformPostService")
  private PlatFormPostService<Map<String, Object>> platFormPostService;

  @Value("${kafka.topic.socialUserProfile}")
  private String topicName;
  
  @Autowired
  private SocioSeerProducer<Map<String, Object>> producer;
  
  @KafkaListener(id = "social-user-consumer", containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.socialUserProfileLookUp}")
  public void consumer(ConsumerRecord<String, String> record) {
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
        producer.produce(topicName, postData);
      } catch (Exception e) {
        log.error("Error while persisting post data", e);
        throw new SocioSeerException("Error while persisting post data", e);
      }
    }
  }
}
