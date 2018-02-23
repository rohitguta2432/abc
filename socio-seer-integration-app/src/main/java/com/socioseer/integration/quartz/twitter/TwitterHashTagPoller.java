package com.socioseer.integration.quartz.twitter;

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

import com.socioseer.integration.config.QuartzConfig;
import com.socioseer.integration.service.impl.twitter.TwitterHashTagsPollerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
public class TwitterHashTagPoller implements Job {

  public static final String JOB_NAME = "TWITTER_HASH_TAG_POLLER_SCHEDULED_JOB";

  @Value("${cron.expression.twitter-hash-tag-poller}")
  private String cronExpression;

  @Autowired
  private TwitterHashTagsPollerService twitterHashTagsPollerService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("Started fetching of twitter hash tags.");
    twitterHashTagsPollerService.pollHashTags();
    log.info("Finished fetching of twitter hash tags.");
  }

  @Bean(name = "twitterHashTagPollerJobDetailBean")
  public JobDetailFactoryBean scheduledJobBean() {
    return QuartzConfig.createJobDetail(this.getClass());
  }

  @Bean(name = "twitterHashPollerJobDetailTrigger")
  public CronTriggerFactoryBean fscheduledPostJobTrigger(
      @Qualifier("twitterHashTagPollerJobDetailBean") JobDetailFactoryBean scheduledJobBean) {
    return QuartzConfig.createCronTrigger(scheduledJobBean.getObject(), cronExpression);
  }

}
