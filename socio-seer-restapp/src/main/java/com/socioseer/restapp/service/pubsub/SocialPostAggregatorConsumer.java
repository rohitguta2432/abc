package com.socioseer.restapp.service.pubsub;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.service.api.CampaignSummaryService;
import com.socioseer.restapp.util.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SocialPostAggregatorConsumer {

  @Autowired
  private CampaignSummaryService campaignSummaryService;

  @KafkaListener(id = "social-post-aggregator-consumer",
      topics = "${kafka.topic.socialPostAggregator}")
  public void consumer(ConsumerRecord<String, String> record) {
    updateRecord(record);
  }

  private void updateRecord(ConsumerRecord<String, String> record) {
    campaignSummaryService.save(parseCampaignSummary(record.value()));
  }

  private CampaignSummary parseCampaignSummary(String campaignSummaryAsString) {
    try {
      return JsonParser.getObject(campaignSummaryAsString, CampaignSummary.class);
    } catch (IOException e) {
      log.error("Error while parsing campaign summary", e);
      throw new SocioSeerException("Error while parsing campaign summary", e);
    }
  }

}
