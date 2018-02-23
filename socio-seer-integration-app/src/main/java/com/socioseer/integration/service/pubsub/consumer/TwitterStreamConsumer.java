package com.socioseer.integration.service.pubsub.consumer;

import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.service.api.PlatFormPostService;
import com.socioseer.integration.service.util.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */

@Slf4j
@Component
public class TwitterStreamConsumer {


  @Autowired
  @Qualifier("platformPostService")
  private PlatFormPostService<Map<String, Object>> platFormPostService;


  @KafkaListener(id = "twitter-stream-consumer-1", topics = "${kafka.topic.twitterStreamTopic}")
  public void consumer1(ConsumerRecord<String, String> record) {
    processRecord(record);
  }


  @KafkaListener(id = "twitter-stream-consumer-2", topics = "${kafka.topic.twitterStreamTopic}")
  public void consumer2(ConsumerRecord<String, String> record) {
    processRecord(record);
  }

  @KafkaListener(id = "twitter-stream-consumer-3", topics = "${kafka.topic.twitterStreamTopic}",
      group = "${kafka.consumer.group.twitterStream}")
  public void consumer3(ConsumerRecord<String, String> record) {
    processRecord(record);
  }


  /**
   * 
   * @param record
   */
  private void processRecord(ConsumerRecord<String, String> record) {

    if (record != null) {
      Map<String, Object> postData = null;
      try {
        String tweetJson = parseTweet(record.value());
        postData = JsonParser.getObject(tweetJson, new TypeReference<Map<String, Object>>() {});
        platFormPostService.save(record.key(), postData);
      } catch (Exception e) {
        log.error("Error while persisting post data", e);
        throw new SocioSeerException("Error while persisting post data", e);
      }
    }

  }


  /**
   * 
   * @param data
   * @return	returns string
   */
  private String parseTweet(String data) {
    String tweetJson = StringEscapeUtils.unescapeJson(data);
    tweetJson = tweetJson.replaceAll("/n", "").replaceAll("/r", "");
    tweetJson = tweetJson.substring(1, tweetJson.length() - 1);
    return tweetJson;
  }
}
