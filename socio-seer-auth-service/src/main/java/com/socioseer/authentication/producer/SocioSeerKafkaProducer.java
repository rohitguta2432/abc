package com.socioseer.authentication.producer;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.socioseer.common.exception.SocioSeerException;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Component
public class SocioSeerKafkaProducer<T> {

  @Autowired
  private KafkaTemplate<String, T> kafkaTemplate;
/**
 * 
 * @param topicName
 * @param message
 */
  public void produce(@NonNull String topicName, @NonNull T message) {
    try {
      kafkaTemplate.send(topicName, message);
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
 * @param message
 */
  public void produce(@NonNull String topicName, @NonNull String key, @NonNull T message) {
    try {
      kafkaTemplate.send(topicName, key, message);
    } catch (Exception e) {
      String msg = String.format("Error while sending message to topic : %s", topicName);
      log.error(msg, e);
      throw new SocioSeerException(msg, e);
    }
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
