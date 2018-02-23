package com.socioseer.integration.quartz;

import lombok.extern.slf4j.Slf4j;

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
import com.socioseer.integration.service.impl.SoicalPostPoller;

@Slf4j
@Component
@DisallowConcurrentExecution
public class SocialPostPollerJob implements Job {

  public static final String JOB_NAME = "FETCH_SOCIAL_POST_IMPRESSION_SCHEDULED_JOB";

  @Value("${cron.expression.social-poller}")
  private String cronExpression;

  @Autowired
  private SoicalPostPoller socialPostPoller;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("Started fetching of social post impressions.");
    socialPostPoller.pollData();
    log.info("Finished fetching of social post impressions.");
  }

  @Bean(name = "socialPollerJobDetailBean")
  public JobDetailFactoryBean fetchScheduledJobBean() {
    return QuartzConfig.createJobDetail(this.getClass());
  }

  @Bean(name = "socialPollerJobDetailTrigger")
  public CronTriggerFactoryBean fetchScheduledPostJobTrigger(
      @Qualifier("socialPollerJobDetailBean") JobDetailFactoryBean fetchScheduledJobBean) {
    return QuartzConfig.createCronTrigger(fetchScheduledJobBean.getObject(), cronExpression);
  }

}
