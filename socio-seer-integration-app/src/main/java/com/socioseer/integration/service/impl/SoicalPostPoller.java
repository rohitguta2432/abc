package com.socioseer.integration.service.impl;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.integration.cache.Cache;
import com.socioseer.integration.config.TwitterConfig;
import com.socioseer.integration.service.TaskExecutor;
import com.socioseer.integration.service.api.SocialPollerService;
import com.socioseer.integration.service.impl.facebook.FBPagePollerServiceImpl;
import com.socioseer.integration.service.impl.twitter.TwitterPollerServiceImpl;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.BeanUtil;

/**
 * <h3>Social Post Poller</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class SoicalPostPoller {

  @Value("${kafka.topic.socialPlatformPostTopic}")
  private String socialPlatformPostTopic;

  @Value("${kafka.topic.socialHandlerTopic}")
  private String socialHandlerTopic;

  @Value("${facebook.field.names}")
  private String fbFieldNames;

  @Autowired
  private Cache cache;

  @Autowired
  private TwitterConfig twitterConfig;

  @Autowired
  private SocioSeerProducer<Map<String, Object>> producer;

  @Autowired
  private BeanUtil beanUtil;

  /**
   * <b>Poll data from twitter and facebook</b>
   */
  public void pollData() {

    List<SocialHandler> socialHandlers =
        cache.getObjects(SocialHandler.OBJECT_KEY, SocialHandler.class);
    if (!CollectionUtils.isEmpty(socialHandlers)) {
      socialHandlers.forEach(sh -> {
        SocialPollerService socialPollerService = null;
        if (sh.getSocialPlatform().getName().equalsIgnoreCase(PlatformConstant.TWITTER)) {
          socialPollerService =
              new TwitterPollerServiceImpl(beanUtil.getBean(HttpClient.class), sh, producer,
                  socialPlatformPostTopic, socialHandlerTopic, twitterConfig);

        } else if (sh.getSocialPlatform().getName().equalsIgnoreCase(PlatformConstant.FACEBOOK)) {
          socialPollerService =
              new FBPagePollerServiceImpl(beanUtil.getBean(HttpClient.class), sh, producer,
                  socialPlatformPostTopic, socialHandlerTopic, fbFieldNames);
        }
        if (socialPollerService != null) {
          TaskExecutor.execute(socialPollerService);
          socialPollerService = null;
        }
      });
    }
  }
}
