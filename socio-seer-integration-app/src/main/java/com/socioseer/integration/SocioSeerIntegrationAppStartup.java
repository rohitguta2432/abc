package com.socioseer.integration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.integration.cache.Cache;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Component
public class SocioSeerIntegrationAppStartup implements ApplicationListener<ApplicationReadyEvent> {


  @Value("${twitter.stream-consumer.key}")
  private String consumerKey;

  @Value("${twitter.stream-consumer.secret}")
  private String consumerSecret;

  @Value("${twitter.stream-access.key}")
  private String accessKey;

  @Value("${twitter.stream-access.secret}")
  private String accessSecret;

  @Value("${kafka.topic.twitterStreamTopic}")
  private String twitterStreamTopicName;

  @Value("${kafka.topic.socialHandlerUserId}")
  private String socialHandlerUserTopic;

  @Autowired
  private Cache cache;

  @Autowired
  private SocioSeerProducer<String> producer;

  private final BlockingQueue<String> queue = new LinkedBlockingDeque<>(10000);
  private final StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

  private static Authentication authentication;
  private static Client client;
  private static final List<Long> twitterUserIds = new ArrayList<>();
  private static boolean isUserAdded = false;

  @PostConstruct
  public void init() {
    authentication = new OAuth1(consumerKey, consumerSecret, accessKey, accessSecret);
    client =
        new ClientBuilder().hosts(Constants.STREAM_HOST).endpoint(endpoint)
            .authentication(authentication).processor(new StringDelimitedProcessor(queue)).build();
    twitterUserIds.addAll(getTwitterUserIds());
  }

  @PreDestroy
  public void destroy() {
    authentication = null;
    client.stop();
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    openStream();
  }

  @KafkaListener(id = "social-handler-user-topic",
      containerFactory = "kafkaListenerContainerFactory",
      topics = "${kafka.topic.socialHandlerUserId}")
  public void consumer(ConsumerRecord<String, String> record) {

    long userId = Long.parseLong(record.value());
    if (!twitterUserIds.contains(userId)) {
      log.info(String.format("Adding user with twitter id : %d to stream polling", userId));
      twitterUserIds.add(userId);
      isUserAdded = true;
    }
  }

  private void openStream() {
    if (!CollectionUtils.isEmpty(twitterUserIds)) {
      endpoint.followings(twitterUserIds);
      while (!client.isDone()) {
        try {
          if (isUserAdded) {
            isUserAdded = false;
            client.reconnect();
          }
          producer.produce(twitterStreamTopicName, PlatformConstant.TWITTER, queue.take());
        } catch (InterruptedException e) {
          log.error("Error while consuming data from twitter stream.");
        } finally {
          log.info("Re-connecting to twitter stream.");
          client.reconnect();
        }
      }
    }
  }

  /**
   * 
   * @return	returns long value
   */
  private List<Long> getTwitterUserIds() {

    List<SocialHandler> socialHandlers =
        cache.getObjects(SocialHandler.OBJECT_KEY, SocialHandler.class);

    if (CollectionUtils.isEmpty(socialHandlers)) {
      return Collections.emptyList();
    }
    return socialHandlers
        .parallelStream()
        .filter(
            handler -> (handler.getSocialPlatform().getName()
                .equalsIgnoreCase(PlatformConstant.TWITTER) && !StringUtils.isEmpty(handler
                .getAccessToken().get(ModelConstants.TWITTER_USER_ID).toString())))
        .map(
            handler -> {
              return Long.parseUnsignedLong(handler.getAccessToken()
                  .get(ModelConstants.TWITTER_USER_ID).toString());
            }).collect(Collectors.toList());
  }
}