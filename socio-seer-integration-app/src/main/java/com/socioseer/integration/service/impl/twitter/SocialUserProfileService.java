package com.socioseer.integration.service.impl.twitter;

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
import com.socioseer.integration.service.impl.TwitterUserProfileLookUp;
import com.socioseer.integration.service.impl.facebook.FacebookPageLookUp;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.BeanUtil;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class SocialUserProfileService {

  @Value("${kafka.topic.socialUserProfileLookUp}")
  private String topicName;

  @Autowired
  private Cache cache;

  @Autowired
  private TwitterConfig twitterConfig;

  @Autowired
  private SocioSeerProducer<Map<String, Object>> producer;

  @Autowired
  private BeanUtil beanUtil;

  /**
   * Method to fetch user details from social sites.
   */
  public void fetch() {
    List<SocialHandler> socialHandlers =
        cache.getObjects(SocialHandler.OBJECT_KEY, SocialHandler.class);
    if (!CollectionUtils.isEmpty(socialHandlers)) {
      socialHandlers.forEach(sh -> {
        if (sh.getSocialPlatform().getName().equalsIgnoreCase(PlatformConstant.TWITTER)) {
          log.info(String.format(
              "Pulling twitter user profile for client : %s with handler id : %s",
              sh.getClientId(), sh.getId()));
          TaskExecutor.execute(new TwitterUserProfileLookUp(beanUtil.getBean(HttpClient.class), sh,
              producer, twitterConfig, topicName));
        }else if (sh.getSocialPlatform().getName().equalsIgnoreCase(PlatformConstant.FACEBOOK)){
          log.info(String.format(
              "Pulling facebook page profile for client : %s with handler id : %s",
              sh.getClientId(), sh.getId()));
          TaskExecutor.execute(new FacebookPageLookUp(beanUtil.getBean(HttpClient.class), sh,
              producer, topicName));
        }
      });
    }
  }
}