package com.socioseer.restapp.service.pubsub;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.service.api.CampaignHashTagSummaryService;
import com.socioseer.restapp.util.JsonParser;

@Slf4j
@Component
public class CampaignHashTagAggregatorConsumer {

  @Autowired
  private CampaignHashTagSummaryService campaignHashTagSummaryService;

  @KafkaListener(id = "user-mention-aggregator-conusmer",
      containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.campaignHashTagSummary}")
  public void consumer(ConsumerRecord<String, String> record) {
    campaignHashTagSummaryService.save(parseCampaignSummary(record.value()));
  }

  private CampaignHashTagSummary parseCampaignSummary(String campaignSummaryAsString) {
    try {
      return JsonParser.getObject(campaignSummaryAsString, CampaignHashTagSummary.class);
    } catch (IOException e) {
      log.error("Error while parsing campaign hash tag summary", e);
      throw new SocioSeerException("Error while parsing campaign hash tag summary", e);
    }
  }

}
