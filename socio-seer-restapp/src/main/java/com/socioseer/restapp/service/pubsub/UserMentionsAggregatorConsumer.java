package com.socioseer.restapp.service.pubsub;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.socioseer.common.domain.model.campaign.summary.UserMentionSummary;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.service.api.UserMentionSummaryService;
import com.socioseer.restapp.util.JsonParser;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMentionsAggregatorConsumer {

  private final UserMentionSummaryService userMentionSummaryService;

  @KafkaListener(id = "campaign-hash-tag-aggregator-conusmer",
      containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.userMentionSummary}")
  public void consumer(ConsumerRecord<String, String> record) {
    userMentionSummaryService.save(parse(record.value()));
  }

  private UserMentionSummary parse(String valueAsString) {
    try {
      return JsonParser.getObject(valueAsString, UserMentionSummary.class);
    } catch (IOException e) {
      log.error("Error while parsing user mention summary", e);
      throw new SocioSeerException("Error while parsing user mention summary", e);
    }
  }

}
