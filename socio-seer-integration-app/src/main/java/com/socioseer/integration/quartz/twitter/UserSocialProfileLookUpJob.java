package com.socioseer.integration.quartz.twitter;

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
import com.socioseer.integration.service.api.JobExecutionInfoService;
import com.socioseer.integration.service.impl.twitter.SocialUserProfileService;

@Component
@DisallowConcurrentExecution
@Slf4j
public class UserSocialProfileLookUpJob implements Job {

  private static final int TWO_HOURS = 2 * 60 * 60 * 1000;

  public static final String JOB_NAME = "USER_PROFILE_SCHEDULED_JOB";

  @Value("${cron.expression.social-profile-lookup}")
  private String cronExpression;

  @Autowired
  private SocialUserProfileService socialUserProfileService;

  @Autowired
  private JobExecutionInfoService jobExecutionInfoService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("Executing aggregation job.");
    long startTime = jobExecutionInfoService.findLastExecutionTime(JOB_NAME);
    long endTime = startTime + TWO_HOURS;
    socialUserProfileService.fetch();
    jobExecutionInfoService.updateByJobName(JOB_NAME, endTime);
    log.info("Aggregation job executed.");
  }

  @Bean(name = "userSocialProfileLookUpBean")
  public JobDetailFactoryBean userSocialProfileLookUpBean() {
    return QuartzConfig.createJobDetail(this.getClass());
  }

  @Bean(name = "userSocialProfileLookUpTrigger")
  public CronTriggerFactoryBean socialPostAggregationTrigger(
      @Qualifier("userSocialProfileLookUpBean") JobDetailFactoryBean fetchScheduledJobBean) {
    return QuartzConfig.createCronTrigger(fetchScheduledJobBean.getObject(), cronExpression);
  }

}
