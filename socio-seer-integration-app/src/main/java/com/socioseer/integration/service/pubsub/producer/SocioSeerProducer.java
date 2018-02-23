package com.socioseer.integration.service.pubsub.producer;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.socioseer.common.exception.SocioSeerException;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Component
public class SocioSeerProducer<T> {

  @Autowired
  private KafkaTemplate<String, T> kafkaTemplate;

  /**
   * 
   * @param topicName
   * @param key
   * @param value
   */
  public void produce(@NonNull String topicName, @NonNull String key, @NonNull T value) {
    try {
      kafkaTemplate.send(topicName, key, value);
    } catch (Exception e) {
      String msg = String.format("Error while sending message to topic : %s", topicName);
      log.error(msg, e);
      throw new SocioSeerException(msg, e);
    }
  }

  /**
   * 
   * @param topicName
   * @param value
   */
  public void produce(@NonNull String topicName, @NonNull T value) {
    try {
      kafkaTemplate.send(topicName, value);
    } catch (Exception e) {
      String msg = String.format("Error while sending message to topic : %s", topicName);
      log.error(msg, e);
      throw new SocioSeerException(msg, e);
    }
  }

  /**
   * 
   * @param topicName
   * @param values
   */
  public void produce(@NonNull String topicName, @NonNull List<T> values) {
    values.stream().forEach(record -> {
      produce(topicName, record);
    });
  }

  /**
   * 
   * @param topicName
   * @param key
   * @param values
   */
  public void produce(@NonNull String topicName, @NonNull String key, @NonNull List<T> values) {
    values.stream().forEach(record -> {
      produce(topicName, key, record);
    });
  }

}
