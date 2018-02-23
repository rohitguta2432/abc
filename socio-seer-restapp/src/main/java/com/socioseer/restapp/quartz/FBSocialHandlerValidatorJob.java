package com.socioseer.restapp.quartz;

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

import com.socioseer.restapp.cache.Cache;
import com.socioseer.restapp.config.QuartzConfig;
import com.socioseer.restapp.service.api.SocialHandlerService;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Component
@Slf4j
@DisallowConcurrentExecution
public class FBSocialHandlerValidatorJob implements Job {

  public static final String JOB_NAME = "FB_SOCIAL_HANDLER_VALIDATOR_JOB";

  
  @Autowired
  private Cache cache;

  @Value("${cron.expression.fb-handler-validator}")
  private String cronExpression;

  @Autowired
  private SocialHandlerService socialHandlerService;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("Started FB token expiration check.");
    socialHandlerService.validateFacebookHandlerTokens();
    log.info("Finished FB token expiration check.");
  }

  /**
   * 
   * @return returns JobDetailFactoryBean
   */
  @Bean(name = "fbHandlerValidatorJob")
  public JobDetailFactoryBean fetchScheduledJobBean() {
    return QuartzConfig.createJobDetail(this.getClass());
  }

  /**
   * 
   * @param fetchScheduledJobBean
   * @return returns CronTriggerFactoryBean
   */
  @Bean(name = "fbHandlerValidatorJobTrigger")
  public CronTriggerFactoryBean fetchScheduledPostJobTrigger(
      @Qualifier("fbHandlerValidatorJob") JobDetailFactoryBean fetchScheduledJobBean) {
    return QuartzConfig.createCronTrigger(fetchScheduledJobBean.getObject(), cronExpression);
  }
  
}
