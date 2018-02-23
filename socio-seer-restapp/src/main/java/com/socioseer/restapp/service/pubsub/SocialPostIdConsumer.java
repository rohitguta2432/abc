package com.socioseer.restapp.service.pubsub;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.util.JsonParser;

@Slf4j
@Component
public class SocialPostIdConsumer {

  @Autowired
  private PostScheduleService postScheduleService;

  @KafkaListener(id = "social-post-id-consumer",
      containerFactory = "kafkaListenerContainerFactory", topics = "${kafka.topic.socialPostId}")
  public void consumeMessage(ConsumerRecord<String, String> record) {
    updatePostScheduleRecord(record);
  }

  private void updatePostScheduleRecord(ConsumerRecord<String, String> record) {
    Map<String, Object> response = parseValue(record.value());
    String postScheduleId = Objects.toString(response.get(SocioSeerConstant.KEY_POST_SCHEDULE_ID));
    String handlerId = Objects.toString(response.get(SocioSeerConstant.KEY_POST_HANDLER_ID));
    String socialPostId = Objects.toString(response.get(SocioSeerConstant.KEY_POST_SOCIAL_ID));
    int status = Integer.valueOf(response.get(ModelConstants.STATUS).toString());
    String message = Objects.toString(response.get(ModelConstants.MESSAGE));
    postScheduleService
        .updateSocialPostId(postScheduleId, handlerId, socialPostId, status, message);
  }

  private Map<String, Object> parseValue(String value) {
    try {
      return JsonParser.getObject(value, new TypeReference<Map<String, Object>>() {});
    } catch (IOException e) {
      String message = "Error while parsing record obtained from kafka topic : socialPostId";
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }
}
