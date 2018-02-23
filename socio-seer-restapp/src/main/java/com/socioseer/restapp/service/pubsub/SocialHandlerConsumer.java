package com.socioseer.restapp.service.pubsub;

import java.io.IOException;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.service.api.SocialHandlerService;
import com.socioseer.restapp.util.JsonParser;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class SocialHandlerConsumer {

  @Autowired
  private SocialHandlerService socialHandlerService;

  @KafkaListener(id = "social-handler-consumer-1",
      containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.socialHandlerTopic}")
  public void consumer_one(ConsumerRecord<String, String> record) {
    socialHandlerService.updateHandlerLastFetch(parseValue(record.value()));
  }


  @KafkaListener(id = "social-handler-consumer-2",
      containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.socialHandlerTopic}")
  public void consumer_two(ConsumerRecord<String, String> record) {
    socialHandlerService.updateHandlerLastFetch(parseValue(record.value()));
  }

  private Map<String, String> parseValue(String value) {
    try {
      return JsonParser.getObject(value, new TypeReference<Map<String, String>>() {});
    } catch (IOException e) {
      String message = "Error while parsing record obtained from kafka topic : socialHandlerTopic";
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

}
