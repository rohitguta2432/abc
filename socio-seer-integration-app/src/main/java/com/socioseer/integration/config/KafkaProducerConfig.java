package com.socioseer.integration.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.socioseer.common.kafka.serializer.KafkaValueSerialzer;

@Configuration
@EnableKafka
public class KafkaProducerConfig<T> {

  @Value("${kafka.servers.bootstrap}")
  private String bootStrapServer;

  @Bean
  public KafkaTemplate<String, T> kafkaTemplate() {
    return new KafkaTemplate<String, T>(producerFactory());
  }

  private ProducerFactory<String, T> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfigs());
  }

  private Map<String, Object> producerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 5000);
    props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
    props.put(ProducerConfig.RETRIES_CONFIG, 3);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaValueSerialzer.class);
    return props;
  }



}
