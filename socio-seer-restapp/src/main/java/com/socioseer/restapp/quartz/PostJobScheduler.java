package com.socioseer.restapp.quartz;

import static com.socioseer.common.constants.ModelConstants.ONE_MINUTE;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.socioseer.common.domain.model.JobExecutionInfo;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.dto.PostScheduleDto;
import com.socioseer.restapp.cache.Cache;
import com.socioseer.restapp.config.QuartzConfig;
import com.socioseer.restapp.service.api.JobExecutionInfoService;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.impl.PostJobSchedulerService;
import com.socioseer.restapp.service.pubsub.SocioSeerKafkaProducer;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Component
@DisallowConcurrentExecution
@Slf4j
public class PostJobScheduler implements Job {

  public static final String JOB_NAME = "POST_JOB_SCHEDULER";

  @Value("${cron.expression.post-schedule}")
  private String cronExpression;

  @Autowired
  private PostScheduleService postScheduleService;

  @Autowired
  private JobExecutionInfoService jobExecutionInfoService;

  @Autowired
  private Cache cache;

  @Autowired
  private PostJobSchedulerService postJobSchedulerService;

  @Autowired
  private SocioSeerKafkaProducer<PostScheduleDto> socialPostProducer;

  @Value("${kafka.topic.socialPost}")
  private String socialPostTopicName;

  @Value("${socio.seer.media.location}")
  private String mediaLocation;

  
  @Override
  public void execute(JobExecutionContext jobExecutionContext) {
    log.info("queuing posts from cache");
    Date startTime = getStartTime();
    Date endTime = new Date(startTime.getTime() + ONE_MINUTE);
    List<PostSchedule> postScheduleList =
        cache.getObjects(PostSchedule.OBJECT_KEY, PostSchedule.class);
    log.info(String.format("Start time = %s, end time = %s", startTime.toString(),
        endTime.toString()));
    if (!CollectionUtils.isEmpty(postScheduleList)) {
      List<PostSchedule> filteredList = filterPosts(startTime, endTime, postScheduleList);
      log.info(String.format("Post schedule list size = %d , filtered list size = %d",
          postScheduleList.size(), CollectionUtils.isEmpty(filteredList) ? 0 : filteredList.size()));
      if (!CollectionUtils.isEmpty(filteredList)) {
        // publishing social post messages to kafka
        log.info("Pushing post schedule to kafka");
        publishPostScheduleToKakfa(postJobSchedulerService.createPostScheduleDtoList(filteredList));
        log.info("Pushed post schedule to kafka");
        cache.delete(PostSchedule.OBJECT_KEY, filteredList.stream().map(item -> {
          item.setExecuted(true);
          return item.getId();
        }).collect(Collectors.toList()));
        postScheduleService.upldateMulti(filteredList);
      }
    }

    jobExecutionInfoService.updateByJobName(JOB_NAME, endTime);

  }
/**
 * 
 * @param postScheduleDtos
 */
  private void publishPostScheduleToKakfa(List<PostScheduleDto> postScheduleDtos) {
    postScheduleDtos.stream().forEach(psd -> {
      socialPostProducer.produce(socialPostTopicName, psd.getPlatform(), psd);
    });
  }
/**
 * 
 * @param startTime
 * @param endTime
 * @param postScheduleList
 * @return returns List of PostSchedule
 */
  private List<PostSchedule> filterPosts(Date startTime, Date endTime,
      List<PostSchedule> postScheduleList) {
    Predicate<PostSchedule> filteredPost =

        postschedule -> postschedule.getRunAt().compareTo(startTime) >= 0
            && postschedule.getRunAt().compareTo(endTime) < 0;
    postScheduleList = postScheduleList.stream().filter(filteredPost).collect(Collectors.toList());
    return postScheduleList;
  }

  /**
   * 
   * @return returns Date
   */
  private Date getStartTime() {
    JobExecutionInfo jobExecutionInfo = jobExecutionInfoService.findByJobName(JOB_NAME);
    if (jobExecutionInfo == null) {
      return new Date();
    }
    return jobExecutionInfo.getLastExecutionTime();
  }

  /**
   * 
   * @return JobDetailFactoryBean
   */
  @Bean(name = "jobWithPutPostTriggerBean")
  public JobDetailFactoryBean postJob() {
    return QuartzConfig.createJobDetail(this.getClass());
  }
/**
 * 
 * @param jobDetail
 * @return CronTriggerFactoryBean
 */
  @Bean(name = "jobWithPutPostTriggerBeanTrigger")
  public CronTriggerFactoryBean postJobTrigger(
      @Qualifier("jobWithPutPostTriggerBean") JobDetail jobDetail) {
    return QuartzConfig.createCronTrigger(jobDetail, cronExpression);
  }

}
