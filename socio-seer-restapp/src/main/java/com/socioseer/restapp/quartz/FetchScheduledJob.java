/**
 * 
 */
package com.socioseer.restapp.quartz;

import static com.socioseer.restapp.service.util.DateUtil.getEndOfDay;
import static com.socioseer.restapp.service.util.DateUtil.getStartOfDay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.dto.HashTagDto;
import com.socioseer.restapp.cache.Cache;
import com.socioseer.restapp.config.QuartzConfig;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.api.PostService;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Component
@Slf4j
@DisallowConcurrentExecution
public class FetchScheduledJob implements Job {

  public static final String JOB_NAME = "FETCH_SCHEDULED_JOB";

  private static final String EVERYDAY_AT_MIDNIGHT = "0 0 0 1/1 * ? *";

  @Autowired
  private PostScheduleService postScheduleService;

  @Autowired
  private CampaignService campaignService;

  @Autowired
  private PostService postService;

  @Autowired
  private Cache cache;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) {

    Date date = new Date();
    Date startDate = getStartOfDay(date);
    Date endDate = getEndOfDay(date);

    log.info("pushing all active scheduled jobs to cache");
    putPostScheduleInCache(startDate, endDate);
    log.info("pushing all active campaign hashtags to cache");
    putCampaignHashTagstoCache(startDate.getTime(), endDate.getTime());
    log.info("Putting all active fb post to cache");
    campaignService.putActiveCampaignFBPostToCache(System.currentTimeMillis());

  }

  /**
   * Putting all active hashtags to cache.
   */
  private void putCampaignHashTagstoCache(long startDate, long endDate) {
    List<Campaign> campaigns = getAllCampaign(startDate, endDate);
    if (!CollectionUtils.isEmpty(campaigns)) {
      List<HashTagDto> hashTagDtos = campaigns.stream().flatMap(campaign -> {
        return createHashTagDtoList(campaign).stream();
      }).collect(Collectors.toList());
      cache.delete(Post.POST_HASH_TAG_KEY);
      cache.multiPut(Post.POST_HASH_TAG_KEY,
          hashTagDtos.stream()
              .collect(Collectors.toMap(HashTagDto::getPostId, Function.identity())));
    }
  }
/**
 * 
 * @param campaign
 * @return returns List of HashTagDto
 */
  private List<HashTagDto> createHashTagDtoList(final Campaign campaign) {
    List<Post> posts = getPostByCampignId(campaign.getId());
    return posts
        .stream()
        .map(
            post -> {
              return createHashTagDto(
                  post.getId(),
                  campaign.getId(),
                  campaign.getClientId(),
                  post.getHashTags().stream()
                      .filter(hashTag -> campaign.getHashtags().contains(hashTag))
                      .collect(Collectors.toList()));
            }).collect(Collectors.toList());
  }

  /**
   * 
   * @param campaignId
   * @return returns List of Post
   */
  private List<Post> getPostByCampignId(String campaignId) {
    return postService.getAllPost(null, Arrays.asList(new Filter(ModelConstants.CAMPAIGN_ID, Arrays
        .asList(campaignId), false, false)));
  }

  /**
   * 
   * @param postId
   * @param campaignId
   * @param clientId
   * @param hashTag
   * @return returns HashTagDto Object
   */
  private HashTagDto createHashTagDto(String postId, String campaignId, String clientId,
      List<String> hashTag) {
    HashTagDto hashTagDto = new HashTagDto(postId, hashTag, clientId);
    hashTagDto.setCampaignId(campaignId);
    return hashTagDto;
  }


  /**
   * 
   * @param startDate
   * @param endDate
   * @return returns List of Campaign
   */
  private List<Campaign> getAllCampaign(long startDate, long endDate) {
    List<Filter> filters = new ArrayList<>();

    filters.add(new Filter("startDate", Arrays.asList("gte", startDate), false, true));
    filters.add(new Filter("endDate", Arrays.asList("lte", endDate), false, true));
    return campaignService.getAllCampaigns(null, filters);
  }

  /**
   * Method to fetch applicable post schedule and put them in cache
   * 
   * @return
   */
  private void putPostScheduleInCache(Date startDate, Date endDate) {

    Optional<List<PostSchedule>> postSchedulerOptional =
        postScheduleService.findByRunAtBetweenAndIsActive(startDate, endDate, true);
    if (postSchedulerOptional.isPresent()) {
      cache.delete(PostSchedule.OBJECT_KEY);
      cache.multiPut(
          PostSchedule.OBJECT_KEY,
          postSchedulerOptional.get().stream()
              .collect(Collectors.toMap(PostSchedule::getId, Function.identity())));
    }
  }

  /**
   * 
   * @return returns JobDetailFactoryBean
   */
  @Bean(name = "jobWithFetchPostTriggerBean")
  public JobDetailFactoryBean fetchScheduledJobBean() {
    return QuartzConfig.createJobDetail(this.getClass());
  }

  /**
   * 
   * @param fetchScheduledJobBean
   * @return returns CronTriggerFactoryBean
   */
  @Bean(name = "jobWithFetchPostTriggerBeanTrigger")
  public CronTriggerFactoryBean fetchScheduledPostJobTrigger(
      @Qualifier("jobWithFetchPostTriggerBean") JobDetailFactoryBean fetchScheduledJobBean) {
    return QuartzConfig.createCronTrigger(fetchScheduledJobBean.getObject(), EVERYDAY_AT_MIDNIGHT);
  }

}
