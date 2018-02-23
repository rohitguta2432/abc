package com.socioseer.integration.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@Configuration
@EnableKafka
public class KakfaConsumerConfig {

  @Value("${kafka.servers.bootstrap}")
  private String bootStrapServer;

  @Value("${kafka.consumer.group.socialPost}")
  private String kafkaConsumerGroup;

  @Bean
  public <T> KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, T>> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, T> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(3);
    factory.getContainerProperties().setPollTimeout(5000);
    return factory;
  }

  @Bean
  public <T> ConsumerFactory<String, T> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs());
  }

  private Map<String, Object> consumerConfigs() {
    Map<String, Object> propsMap = new HashMap<>();
    propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
    propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroup);
    propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    return propsMap;
  }
}
