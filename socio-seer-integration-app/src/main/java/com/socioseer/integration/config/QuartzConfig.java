package com.socioseer.integration.config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.CollectionUtils;

@Configuration
public class QuartzConfig {

  private static final String QUARTZ_PROPERTIES = "/quartz.properties";

  @Bean
  public JobFactory jobFactory(ApplicationContext applicationContext) {
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory,
      List<Trigger> triggers) throws IOException {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setOverwriteExistingJobs(true);
    factory.setAutoStartup(true);
    factory.setDataSource(dataSource);
    factory.setJobFactory(jobFactory);
    factory.setQuartzProperties(quartzProperties());
    factory.setWaitForJobsToCompleteOnShutdown(true);
    if (!CollectionUtils.isEmpty(triggers)) {
      factory.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
    }
    return factory;
  }

  @Bean
  public Properties quartzProperties() throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
    propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_PROPERTIES));
    propertiesFactoryBean.afterPropertiesSet();
    return propertiesFactoryBean.getObject();
  }

  public static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs) {
    SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
    factoryBean.setJobDetail(jobDetail);
    factoryBean.setStartDelay(0L);
    factoryBean.setRepeatInterval(pollFrequencyMs);
    factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    factoryBean
        .setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
    return factoryBean;
  }

  public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
    CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
    factoryBean.setJobDetail(jobDetail);
    factoryBean.setCronExpression(cronExpression);
    factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
    return factoryBean;
  }

  @SuppressWarnings("rawtypes")
  public static JobDetailFactoryBean createJobDetail(Class jobClass) {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(jobClass);
    factoryBean.setDurability(true);
    return factoryBean;
  }

}
