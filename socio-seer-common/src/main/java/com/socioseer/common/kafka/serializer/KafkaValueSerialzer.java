package com.socioseer.common.kafka.serializer;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.socioseer.common.exception.SocioSeerException;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class KafkaValueSerialzer<T> implements Serializer<T> {

  private ObjectMapper objectMapper;

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> config, boolean iskey) {
    configure(new KafkaJsonSerializerConfig(config));
  }

  protected void configure(KafkaJsonSerializerConfig config) {
    boolean prettyPrint = config.getBoolean(KafkaJsonSerializerConfig.JSON_INDENT_OUTPUT);
    this.objectMapper = new ObjectMapper();
    this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
  }

  @Override
  public byte[] serialize(String tppic, T data) {
    if (data == null) {
      return null;
    }

    try {
      return objectMapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new SocioSeerException("Error serializing JSON message", e);
    }
  }
}
