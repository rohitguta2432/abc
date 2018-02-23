package com.socioseer.restapp.service.queue;

import static com.socioseer.common.constants.ModelConstants.FAN_COUNT;
import static com.socioseer.common.constants.ModelConstants.FOLLOWERS_COUNT;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.domain.model.ClientFollowersInfo;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.service.api.ClientFollowersInfoService;
import com.socioseer.restapp.util.JsonParser;

@Slf4j
@Component
public class SocialUserProfileConsumer {

  @Autowired
  private ClientFollowersInfoService clientFollowersInfoService;
  
  @KafkaListener(id = "social-user-consumer", containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.socialUserProfile}")
  public void consumer(ConsumerRecord<String, String> record) {
    processRecord(record);
  }

  private void processRecord(ConsumerRecord<String, String> record) {
    Map<String, Object> response = parseValue(record.value());
    String handlerId = Objects.toString(response.get(SocioSeerConstant.KEY_POST_HANDLER_ID));
    String clientId = Objects.toString(response.get(SocioSeerConstant.KEY_CLIENT_ID));
    String platformId = Objects.toString(response.get(SocioSeerConstant.KEY_PLATFORM));
    long followersCount = getFollowersCount(response);
    ClientFollowersInfo clientFollowersInfo = build(handlerId, clientId, platformId, followersCount);
    clientFollowersInfoService.save(clientFollowersInfo);
  }

  private long getFollowersCount(Map<String, Object> response) {
    long followersCount = 0L;
    if(response.containsKey(FAN_COUNT)){
      followersCount = (int)response.get(FAN_COUNT);
    }else{
      followersCount = (int)response.get(FOLLOWERS_COUNT);
    }
    return followersCount;
  }

  private ClientFollowersInfo build(String handlerId, String clientId, String platformId, long followersCount) {
    ClientFollowersInfo clientFollowersInfo = new ClientFollowersInfo();
    clientFollowersInfo.setClientId(clientId);
    clientFollowersInfo.setFollowersCount(followersCount);
    clientFollowersInfo.setHandlerId(handlerId);
    clientFollowersInfo.setPlatform(platformId);
    return clientFollowersInfo;
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
