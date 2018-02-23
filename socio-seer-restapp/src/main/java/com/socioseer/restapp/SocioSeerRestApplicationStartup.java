package com.socioseer.restapp;

import static com.socioseer.restapp.service.util.DateUtil.getEndOfDay;
import static com.socioseer.restapp.service.util.DateUtil.getStartOfDay;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.restapp.cache.Cache;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.api.SocialHandlerService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */

@Component
@Slf4j
public class SocioSeerRestApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

  /** The social handler service. */
  @Autowired
  private SocialHandlerService socialHandlerService;

  /** The post schedule service. */
  @Autowired
  private PostScheduleService postScheduleService;

  /** The cache. */
  @Autowired
  private Cache cache;

  @Autowired
  private CampaignService campaignService;

  /**
   * {@inheritDoc}
   */
  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    // clearCache();
    addHandlersToCache();
    addScheduledPostToCache();
    long currentTime = System.currentTimeMillis();
    campaignService.getActiveCampaignHashTags(currentTime);
    campaignService.putCampaignIntoCacheOnApplicationStartup();
    return;
  }

  private void clearCache() {
    cache.delete(PostSchedule.OBJECT_KEY);
  }

  /**
   * Adds the handlers to cache.
   */
  private void addHandlersToCache() {
    log.info("adding all Social Handlers from Repository to cache");
    List<SocialHandler> socialHandlers = socialHandlerService.getSocialHandlers();
    Map<String, SocialHandler> socialHandlerMap = getSocialHandlerIdMap(socialHandlers);
    cache.delete(SocialHandler.OBJECT_KEY);
    cache.multiPut(SocialHandler.OBJECT_KEY, socialHandlerMap);
  }

  /**
   * Adds the scheduled post to cache.
   */
  private void addScheduledPostToCache() {
    log.info("pushing all active scheduled jobs to cache");
    cache.delete(PostSchedule.OBJECT_KEY);
    Date date = new Date();
    Date startDate = getStartOfDay(date);
    Date endDate = getEndOfDay(date);
    Optional<List<PostSchedule>> postSchedulerOptional = postScheduleService
        .findByRunAtBetweenAndIsExecutedAndIsActive(startDate, endDate, false, true);
    if (postSchedulerOptional.isPresent()
        && !CollectionUtils.isEmpty(postSchedulerOptional.get())) {
      cache.multiPut(PostSchedule.OBJECT_KEY, postSchedulerOptional.get().stream()
          .collect(Collectors.toMap(PostSchedule::getId, Function.identity())));
    }

  }

  /**
   * Gets the social handler id map.
   *
   * @param socialHandlers the social handlers
   * @return the social handler id map
   */
  private Map<String, SocialHandler> getSocialHandlerIdMap(List<SocialHandler> socialHandlers) {
    return socialHandlers.stream()
        .collect(Collectors.toMap(SocialHandler::getId, Function.identity()));
  }

}
