package com.socioseer.integration.quartz.facebook;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.HttpClient;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.dto.FBPostDto;
import com.socioseer.integration.cache.Cache;
import com.socioseer.integration.config.QuartzConfig;
import com.socioseer.integration.service.TaskExecutor;
import com.socioseer.integration.service.impl.facebook.FBPostPollerService;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;
import com.socioseer.integration.service.util.BeanUtil;

@Slf4j
@Component
@DisallowConcurrentExecution
public class FacebookPostPoller implements Job {

  @Value("${cron.expression.social-poller}")
  private String cronExpression;

  @Value("${kafka.topic.socialPlatformPostTopic}")
  private String socialPlatformPostTopic;

  @Value("${facebook.field.names}")
  private String fbFieldNames;

  @Autowired
  private Cache cache;

  @Autowired
  private BeanUtil beanUtil;

  @Autowired
  private SocioSeerProducer<Map<String, Object>> producer;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("Polling of facebook post started.");
    List<SocialHandler> facebookSocialHandlers = getFaceBookHandlers();
    facebookSocialHandlers.forEach(sh -> {
      Optional<List<FBPostDto>> optional =
          cache.get(FBPostDto.CACHE_OBJECT, sh.getId(), new TypeReference<List<FBPostDto>>() {});

      if (optional.isPresent()) {
        TaskExecutor.execute(new FBPostPollerService(beanUtil.getBean(HttpClient.class), sh,
            producer, socialPlatformPostTopic, fbFieldNames, optional.get()));
      }
    });
    log.info("Polling of facebook post finished.");
  }

  private List<SocialHandler> getFaceBookHandlers() {
    List<SocialHandler> socialHandlers =
        cache.getObjects(SocialHandler.OBJECT_KEY, SocialHandler.class);
    if (CollectionUtils.isEmpty(socialHandlers)) {
      return Collections.emptyList();
    }

    return socialHandlers.stream()
        .filter(sh -> PlatformConstant.FACEBOOK.equalsIgnoreCase(sh.getSocialPlatform().getName()))
        .collect(Collectors.toList());
  }

  @Bean(name = "facebookPostPollerBean")
  public JobDetailFactoryBean socialPostAggregationBean() {
    return QuartzConfig.createJobDetail(this.getClass());
  }

  @Bean(name = "facebookPostPollerTrigger")
  public CronTriggerFactoryBean socialPostAggregationTrigger(
      @Qualifier("facebookPostPollerBean") JobDetailFactoryBean fetchScheduledJobBean) {
    return QuartzConfig.createCronTrigger(fetchScheduledJobBean.getObject(), cronExpression);
  }


}
