package com.socioseer.integration.service.pubsub.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.socioseer.integration.service.SocialPostProcessor;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Component
public class SocialPostDtoConsumer {

  @Autowired
  private SocialPostProcessor socialPostProcessor;

  @KafkaListener(id = "social-post-dto-consumer",
      containerFactory = "kafkaListenerContainerFactory", topics = "${kafka.topic.socialPost}")
  public void consumer(ConsumerRecord<String, String> record) {
    socialPostProcessor.process(record);
  }
}
