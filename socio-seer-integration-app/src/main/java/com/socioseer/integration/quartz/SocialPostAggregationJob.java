package com.socioseer.integration.quartz;

import java.util.List;

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

import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;
import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;
import com.socioseer.common.domain.model.campaign.summary.UserMentionSummary;
import com.socioseer.integration.config.QuartzConfig;
import com.socioseer.integration.service.api.JobExecutionInfoService;
import com.socioseer.integration.service.api.SocialPostAggregator;
import com.socioseer.integration.service.pubsub.producer.SocioSeerProducer;

import lombok.extern.slf4j.Slf4j;

@Component
@DisallowConcurrentExecution
@Slf4j
public class SocialPostAggregationJob implements Job {

  public static final String JOB_NAME = "SOCIAL_POST_AGGREGATION_SCHEDULED_JOB";

  @Value("${cron.expression.post-aggregator}")
  private String cronExpression;

  @Value("${kafka.topic.socialPostAggregator}")
  private String socialPostAggregatorTopic;

  @Value("${kafka.topic.campaignHashTagSummary}")
  private String campaignHashTagSummaryTopic;

  @Value("${kafka.topic.userMentionSummary}")
  private String userMentionSummaryTopic;

  @Autowired
  @SuppressWarnings("rawtypes")
  private SocioSeerProducer producer;

  @Qualifier("twitterPostAggregator")
  @Autowired
  private SocialPostAggregator<List<CampaignSummary>> twitterSocialPostAggregator;

  @Qualifier("facebookPostAggregator")
  @Autowired
  private SocialPostAggregator<List<CampaignSummary>> facebookSoicalPostAggregator;

  @Autowired
  private SocialPostAggregator<List<CampaignHashTagSummary>> twitterHashTagAggregator;

  @Autowired
  private SocialPostAggregator<List<UserMentionSummary>> userMentionSummaryAggregator;

  @Autowired
  private JobExecutionInfoService jobExecutionInfoService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("Executing aggregation job.");
    long startTime = jobExecutionInfoService.findLastExecutionTime(JOB_NAME);
    log.info("Aggregating campaign summary data.");
    aggregateCampaignSummary(startTime);
    log.info("Aggregating campaign hash tag data.");
    aggregateCampaignHashTagData(startTime);
    log.info("Aggregating user mentions data.");
    aggregateUserMentionData(startTime);
    jobExecutionInfoService.updateByJobName(JOB_NAME, System.currentTimeMillis());
    log.info("Aggregation job executed.");
  }

  @SuppressWarnings("unchecked")
  private void aggregateUserMentionData(long lastCapturedAt) {
    List<UserMentionSummary> userMentionSummaries =
        userMentionSummaryAggregator.aggregateData(lastCapturedAt);
    if (!CollectionUtils.isEmpty(userMentionSummaries)) {
      producer.produce(userMentionSummaryTopic, userMentionSummaries);
    }
  }

  @SuppressWarnings("unchecked")
  private void aggregateCampaignHashTagData(long startTime) {
    List<CampaignHashTagSummary> campaignHashTagSummaries =
        twitterHashTagAggregator.aggregateData(startTime);
    if (!CollectionUtils.isEmpty(campaignHashTagSummaries)) {
      producer.produce(campaignHashTagSummaryTopic, campaignHashTagSummaries);
    }
  }

  @SuppressWarnings("unchecked")
  private void aggregateCampaignSummary(long startTime) {
    List<CampaignSummary> campaignSummaryList =
        twitterSocialPostAggregator.aggregateData(startTime);
    campaignSummaryList.addAll(facebookSoicalPostAggregator.aggregateData(startTime));
    if (!CollectionUtils.isEmpty(campaignSummaryList)) {
      producer.produce(socialPostAggregatorTopic, campaignSummaryList);
    }
  }

  @Bean(name = "socialPostAggregationBean")
  public JobDetailFactoryBean socialPostAggregationBean() {
    return QuartzConfig.createJobDetail(this.getClass());
  }

  @Bean(name = "socialPostAggregationTrigger")
  public CronTriggerFactoryBean socialPostAggregationTrigger(
      @Qualifier("socialPostAggregationBean") JobDetailFactoryBean fetchScheduledJobBean) {
    return QuartzConfig.createCronTrigger(fetchScheduledJobBean.getObject(), cronExpression);
  }

}
