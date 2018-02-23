package com.socioseer.restapp.service.impl;

import static com.socioseer.common.constants.StatusConstants.CAMPAIGN_POST_CREATED;
import static com.socioseer.common.constants.StatusConstants.POST_SCHEDULE_FREQUENCY_CUSTOM;
import static com.socioseer.common.constants.StatusConstants.POST_SCHEDULE_FREQUENCY_FOUR_PER_HOUR;
import static com.socioseer.common.constants.StatusConstants.POST_SCHEDULE_FREQUENCY_ONE_PER_HOUR;
import static com.socioseer.common.constants.StatusConstants.POST_SCHEDULE_FREQUENCY_THREE_PER_HOUR;
import static com.socioseer.common.constants.StatusConstants.POST_SCHEDULE_FREQUENCY_TWELVE_PER_HOUR;
import static com.socioseer.common.constants.StatusConstants.POST_SCHEDULE_FREQUENCY_TWENTY_FOUR_PER_HOUR;
import static com.socioseer.common.constants.StatusConstants.POST_SCHEDULE_FREQUENCY_TWO_PER_HOUR;
import static com.socioseer.restapp.quartz.PostJobScheduler.JOB_NAME;
import static com.socioseer.restapp.service.util.DateUtil.getEndOfDay;
import static com.socioseer.restapp.service.util.DateUtil.getStartOfDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.mongodb.WriteResult;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.NotificationMeaage;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.model.Alert;
import com.socioseer.common.domain.model.JobExecutionInfo;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.campaign.SocialPlatform;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostHandler;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.domain.model.request.PostScheduleRequest;
import com.socioseer.common.domain.model.request.ScheduleTime;
import com.socioseer.common.dto.FBPostDto;
import com.socioseer.common.dto.HashTagDto;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.cache.Cache;
import com.socioseer.restapp.dao.api.PostScheduleDao;
import com.socioseer.restapp.service.api.AlertService;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.service.api.JobExecutionInfoService;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.api.PostService;
import com.socioseer.restapp.service.api.SocialPlatformService;
import com.socioseer.restapp.service.api.TaskService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.DateUtil;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * <h3>PostScheduleService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Service
@Slf4j
public class PostScheduleServiceImpl implements PostScheduleService {

  private static final String DATE_PATTERN = "dd/MM/yyyy HH:mm";

  @Autowired
  private PostService postService;

  @Autowired
  private PostScheduleDao postScheduleDao;

  @Autowired
  private SocialPlatformService socialPlatformService;

  @Autowired
  private Cache cache;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private AlertService notificationService;

  @Autowired
  private JobExecutionInfoService jobExecutionInfoService;

  @Autowired
  private CampaignService campaignService;

  @Autowired
  private UserService userService;
  
  @Autowired
  private TaskService taskService;


  /**
   * <b>Save postScheduleRequests list </b>
   * @param postId
   * @param postScheduleRequests
   * @return	returns PostSchedulelist
   */
  @Async
  @Override
  public List<PostSchedule> save(String postId, List<PostScheduleRequest> postScheduleRequests) {
    try {
      Post post = getPost(postId);
      if(!ObjectUtils.isEmpty(post)){
      postScheduleDao.deleteByPost(post);
      taskService.deleteByPostId(post.getId());
      }
      List<PostSchedule> postSchedules = new ArrayList<>();
      postScheduleRequests.forEach(postScheduleRequest -> {
        SocialPlatform socialPlatform = getSocialPlatform(postScheduleRequest.getPlatformId());
        validatePostSchedule(postScheduleRequest);
        List<PostSchedule> postScheduleList = prepare(post, socialPlatform, postScheduleRequest);
        createAlert(post, ModelConstants.NOTIFICATION_TASK,
            NotificationMeaage.MESSAGE_FOR_SCHEDULE, StatusConstants.NOTIFICATION_NOT_VIEWED,
            NotificationMeaage.DESCRIPTION_FOR_SCHEDULE);
        postSchedules.addAll(postScheduleDao.save(postScheduleList));
      });

      if (!CollectionUtils.isEmpty(postSchedules)) {
        post.setStatus(CAMPAIGN_POST_CREATED);
        post.setPostScheduleRequests(postScheduleRequests);
        postService.updatePostScheduler(postId, post);
      }
      return postSchedules;
    } catch (Exception e) {
      String message =
          String.format("Error while saving schedule details for post id : %s", postId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Active Post by postId</b>
   * @param postId
   * @param updatedBy
   */
  @Override
  public synchronized void setActive(@NonNull String postId, String updatedBy) {
    try {
      Optional<List<PostSchedule>> postScheduleOptList = findByPostId(postId);
      postScheduleOptList.ifPresent(postScheduleList -> {
        postScheduleList.stream().forEach(postschedule -> {
          postschedule.setActive(true);
          postschedule.setUpdatedBy(updatedBy);
        });
      });
      List<PostSchedule> postScheduleList = postScheduleOptList.get();
      if (CollectionUtils.isEmpty(postScheduleList)) {
        return;
      }
      validateRunAtTime(postScheduleList);
      postScheduleDao.save(postScheduleList);
      addHashTagsToCache(postId);
      scheduledTodaysPost(postScheduleList);
    } catch (Exception e) {
      String message =
          String.format("Error while saving schedule details for post id : %s", postId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * 
   * @param postId
   */
  private void addHashTagsToCache(String postId) {
    Post post = postService.get(postId);

    if (CollectionUtils.isEmpty(post.getHashTags())) {
      return;
    }

    List<String> hashTags = Lists.newArrayList();
    if (!StringUtils.isEmpty(post.getCampaignId())) {
      Campaign campaign = campaignService.get(post.getCampaignId());
      if (!CollectionUtils.isEmpty(campaign.getHashtags())
          && !CollectionUtils.isEmpty(post.getHashTags())) {
        hashTags.addAll(post.getHashTags().stream()
            .filter(hashTag -> campaign.getHashtags().contains(hashTag))
            .collect(Collectors.toList()));
      }
    }

    if (CollectionUtils.isEmpty(hashTags) && !CollectionUtils.isEmpty(post.getHashTags())) {
      hashTags.addAll(post.getHashTags());
    }
    HashTagDto hashTagDto = new HashTagDto(postId, hashTags, post.getClientId());
    hashTagDto.setCampaignId(post.getCampaignId());
    cache.put(Post.POST_HASH_TAG_KEY, postId, hashTagDto);

  }

  /**
   * 
   * @param postScheduleList
   */
  private void validateRunAtTime(List<PostSchedule> postScheduleList) {
    Date date = getTime();
    postScheduleList.get(0).getRunAt().compareTo(date);
    Predicate<PostSchedule> postSchedulePredicate = postSchedule -> {
      return postSchedule.getRunAt().compareTo(date) > 0 ? true : false;
    };
    List<PostSchedule> postToScheduleList =
        postScheduleList.stream().filter(postSchedulePredicate).collect(Collectors.toList());
    if (CollectionUtils.isEmpty(postToScheduleList)) {
      throw new SocioSeerException("Post scheduled date missed.");
    }
  }

  /**
   * 
   * @return	returns Date
   */
  private Date getTime() {
    JobExecutionInfo jobExecutionInfo = jobExecutionInfoService.findByJobName(JOB_NAME);
    if (jobExecutionInfo == null) {
      return new Date();
    }
    return jobExecutionInfo.getLastExecutionTime();
  }

  /**
   * <b>Get PostSchedule list by postId</b>
   * @param postId
   * @return	returns PostSchedule list
   */
  @Override
  public Optional<List<PostSchedule>> findByPostId(String postId) {
    Post post = getPost(postId);
    Optional<List<PostSchedule>> postScheduleOptList = postScheduleDao.findByPost(post);
    if (!postScheduleOptList.isPresent()) {
      throw new SocioSeerException("No Post Schedule found for postId" + postId);
    }
    return postScheduleOptList;
  }

  /**
   * <b>Update postScheduleList</b>
   * @param postScheduleList
   */
  @Override
  public void upldateMulti(List<PostSchedule> postScheduleList) {
    if (CollectionUtils.isEmpty(postScheduleList)) {
      throw new IllegalArgumentException("updated list can not be empty");
    }
    postScheduleDao.save(postScheduleList);
  }

  /**
   * <b>Get PostSchedule list by startDate and endDate and active status</b>
   * @param startDate
   * @param endDate
   * @param isActive
   * @return	returns PostSchedule list
   */
  @Override
  public Optional<List<PostSchedule>> findByRunAtBetweenAndIsActive(Date startDate, Date endDate,
      boolean isActive) {
    return postScheduleDao.findByRunAtBetweenAndIsActive(startDate, endDate, isActive);
  }

  /**
   * <b>Get PostSchedule list by startData and endDate and isExecuted and isActive</b>
   * @param startDate
   * @param endDate
   * @param isExecuted
   * @param isActive
   * @return	returns PostSchedule list
   */
  @Override
  public Optional<List<PostSchedule>> findByRunAtBetweenAndIsExecutedAndIsActive(Date startDate,
      Date endDate, boolean isExecuted, boolean isActive) {
    return postScheduleDao.findByRunAtBetweenAndIsExecutedAndIsActive(startDate, endDate,
        isExecuted, isActive);
  }

  /**
   * <b>Update Post Status</b>
   * @param postScheduleId
   * @param handlerId
   * @param socialPostId
   * @param statusCode
   * @param message
   */
  @Override
  public void updateSocialPostId(@NonNull String postScheduleId, @NonNull String handlerId,
      @NonNull String socialPostId, int statusCode, String message) {
    Query query =
        new Query(Criteria.where(ModelConstants.ID).is(postScheduleId)
            .and(ModelConstants.POST_HANDLERS_HANDLE_ID).is(handlerId));
    Update update = new Update();
    if (!StringUtils.isEmpty(socialPostId)) {
      update.push(ModelConstants.UDATE_POST_HANDLERS_SOCIAL_POST_ID, socialPostId);
    }
    update.set(ModelConstants.UDATE_POST_HANDLERS_SOCIAL_POST_STATUS, statusCode);
    update.set(ModelConstants.UDATE_POST_HANDLERS_SOCIAL_POST_MESSAGE, message);
    WriteResult writeResult = mongoTemplate.updateFirst(query, update, PostSchedule.class);
    log.debug(String.format("Rows Updated : %d for post schedule id : %s and handler id : %s",
        writeResult.getN(), postScheduleId, handlerId));

    putPostIntoCache(postScheduleId, socialPostId, handlerId);
  }

  /**
   * <b>Put post into cache </b>
   * @param postScheduleId
   * @param socialPostId
   * @param handlerId
   */
  private void putPostIntoCache(String postScheduleId, String socialPostId, String handlerId) {
    PostSchedule postSchedule = postScheduleDao.findOne(postScheduleId);
    Post post = postSchedule.getPost();
    Campaign campaign = campaignService.get(post.getCampaignId());
    FBPostDto fbPostDto =
        new FBPostDto(post.getId(), socialPostId, postScheduleId, campaign.getId());

    Optional<List<FBPostDto>> optional =
        cache.get(FBPostDto.CACHE_OBJECT, handlerId, new TypeReference<List<FBPostDto>>() {});

    List<FBPostDto> dtos = null;

    if (optional.isPresent()) {
      dtos = optional.get();
      dtos.add(fbPostDto);
    } else {
      dtos = new ArrayList<>();
      dtos.add(fbPostDto);
    }
    cache.put(FBPostDto.CACHE_OBJECT, handlerId, dtos);
  }

  /**
   * <b>Get PostSchedule by socialPostId</b>
   * @param socialPostId
   * @return	returns PostSchedule
   */
  @Override
  public PostSchedule getPostScheduleBySocialPostId(@NonNull String socialPostId) {
    Query query =
        new Query(Criteria.where(ModelConstants.POST_HANDLERS_SOCIAL_POST_ID).in(socialPostId));
    return mongoTemplate.findOne(query, PostSchedule.class);
  }

  /**
   * <b>Get SocialPlatform by platformId</b>
   * @param platformId
   * @return	returns SocialPlatform
   */
  private SocialPlatform getSocialPlatform(String platformId) {
    if (StringUtils.isEmpty(platformId)) {
      log.info("Platform cannot be empty/null");
      throw new IllegalArgumentException("platform Id can not be empty/null");
    }
    SocialPlatform socialPlatform = socialPlatformService.get(platformId);
    if (ObjectUtils.isEmpty(socialPlatform)) {
      log.info("Invalid platform for platformId " + platformId);
      throw new IllegalArgumentException("Platform Id can not be empty/null");
    }
    return socialPlatform;
  }

  /**
   * <b>Get PostSchedule list by post and socialPlatform and postScheduleRequest</b>
   * @param post
   * @param socialPlatform
   * @param postScheduleRequest
   * @return	returns PostSchedule list
   */
  private List<PostSchedule> prepare(Post post, SocialPlatform socialPlatform,
      PostScheduleRequest postScheduleRequest) {
    List<Date> runAtList = getRunTime(postScheduleRequest);
    if (CollectionUtils.isEmpty(runAtList)) {
      throw new SocioSeerException("Not able to collect dates from request");
    }

    List<PostHandler> postHandlers =
        post.getSocialHandlers().stream()
            .filter(sh -> sh.getSocialPlatform().getId().equals(socialPlatform.getId()))
            .map(sh -> new PostHandler(sh.getId())).collect(Collectors.toList());

    /*
     * List<PostHandler> postHandlers = post.getSocialHandlers().stream().map(sh -> new
     * PostHandler(sh.getId())) .collect(Collectors.toList());
     */
    long createdDate = DateUtil.getCurrentTimeInMilliseconds();

    return runAtList
        .stream()
        .map(
            runAt -> new PostSchedule(post, socialPlatform, postHandlers, runAt, false, false,
                createdDate, postScheduleRequest.getCreatedBy(), postScheduleRequest.getClientId(),
                postScheduleRequest.getClientName(), postScheduleRequest.getCampaignId(), post
                    .getCampaignTitle())).collect(Collectors.toList());
  }

  /**
   * <b>Get RunTime of postScheduleRequest</b>
   * @param postScheduleRequest
   * @return	returns Date list
   */
  private List<Date> getRunTime(PostScheduleRequest postScheduleRequest) {
    List<Date> runAtList = new ArrayList<Date>();
    int frequencyCode = postScheduleRequest.getFrequencyCode();
    long duration = 60 * 60 * 1000;
    int frequency = 1;
    switch (frequencyCode) {
      case POST_SCHEDULE_FREQUENCY_CUSTOM:
        runAtList =
            postScheduleRequest.getScheduleTime().stream()
                .map(scheduleTime -> DateUtil.getDate(scheduleTime.getStartTime(), DATE_PATTERN))
                .collect(Collectors.toList());
        break;
      case POST_SCHEDULE_FREQUENCY_ONE_PER_HOUR:
        break;
      case POST_SCHEDULE_FREQUENCY_TWO_PER_HOUR:
        frequency = 2;
        break;
      case POST_SCHEDULE_FREQUENCY_THREE_PER_HOUR:
        frequency = 3;
        break;
      case POST_SCHEDULE_FREQUENCY_FOUR_PER_HOUR:
        frequency = 4;
        break;
      case POST_SCHEDULE_FREQUENCY_TWELVE_PER_HOUR:
        frequency = 12;
        break;
      case POST_SCHEDULE_FREQUENCY_TWENTY_FOUR_PER_HOUR:
        frequency = 24;
        break;
      default:
        throw new IllegalArgumentException("Invalid frequency "
            + postScheduleRequest.getFrequencyCode());
    }
    if (CollectionUtils.isEmpty(runAtList)) {
      List<ScheduleTime> scheduleTimeList = postScheduleRequest.getScheduleTime();
      Date startDate = DateUtil.getDate(scheduleTimeList.get(0).getStartTime(), DATE_PATTERN);
      Date endDate = DateUtil.getDate(scheduleTimeList.get(0).getEndTime(), DATE_PATTERN);
      runAtList = DateUtil.splitDuration(startDate, endDate, duration * frequency);
    }
    return runAtList;
  }

  /**
   * <b>Get Post by postId</b>
   * @param postId
   * @return	returns Post
   */
  private Post getPost(String postId) {
    if (StringUtils.isEmpty(postId)) {
      log.info("Post id can not be empty/null");
      throw new IllegalArgumentException("Post id can not be empty/null");
    }

    Post post = postService.get(postId);
    if (ObjectUtils.isEmpty(post)) {
      log.info(String.format("Invalid post id ", postId));
      throw new IllegalArgumentException(String.format("Invalid post id ", postId));
    }
    return post;
  }

  /**
   * <b>Validate PostScheduleRequest </b>
   * @param postScheduleRequest
   */
  private void validatePostSchedule(PostScheduleRequest postScheduleRequest) {
    if (StringUtils.isEmpty(postScheduleRequest.getClientId())) {
      log.info("Client id cannot be empty/null");
      throw new IllegalArgumentException("Client id cannot be empty/null");
    }

    if (StringUtils.isEmpty(postScheduleRequest.getPlatformId())) {
      log.info("Latform can not be empty/null");
      throw new IllegalArgumentException("platform can not be empty/null");
    }

    if (CollectionUtils.isEmpty(postScheduleRequest.getScheduleTime())) {
      log.info("Limings can not be empty/null");
      throw new IllegalArgumentException("timings can not be empty/null");
    }

    if (StringUtils.isEmpty(postScheduleRequest.getCreatedBy())) {
      log.info("Created by can not be empty/null");
      throw new IllegalArgumentException("Created by can not be empty/null");
    }

    List<ScheduleTime> scheduleTimeList = postScheduleRequest.getScheduleTime();
    for (ScheduleTime scheduleTime : scheduleTimeList) {
      if (!DateUtil.isValidDate(scheduleTime.getStartTime(), DATE_PATTERN)
          || !DateUtil.isValidDate(scheduleTime.getStartTime(), DATE_PATTERN)) {
        log.info("Invalid date " + scheduleTime);
        throw new IllegalArgumentException("Invalid date " + scheduleTime);
      }
    }
  }

  /**
   * <b>Scheduled Todays Post </b>
   * @param postScheduleList
   */
  private void scheduledTodaysPost(List<PostSchedule> postScheduleList) {
    Date date = new Date();
    Date startDate = getStartOfDay(date);
    Date endDate = getEndOfDay(date);
    Predicate<PostSchedule> postSchedulePredicate =
        postSchedule -> postSchedule.getRunAt().compareTo(startDate) >= 0
            && postSchedule.getRunAt().compareTo(endDate) < 0;
    List<PostSchedule> postToScheduleList =
        postScheduleList.stream().filter(postSchedulePredicate).collect(Collectors.toList());
    if (CollectionUtils.isEmpty(postToScheduleList)) {
      return;
    }
    cache.multiPut(
        PostSchedule.OBJECT_KEY,
        postScheduleList.stream().collect(
            Collectors.toMap(PostSchedule::getId, Function.identity())));
  }

  /**
   * <b>Get Published Post by postId</b>
   * @param postId
   * @return	returns PostSchedule list
   */
  @Override
  public List<PostSchedule> findPublishedPost(String postId) {
    Post post = getPost(postId);
    Optional<List<PostSchedule>> postScheduleOptList =
        postScheduleDao.findByPostAndIsExecuted(post, true);
    if (!postScheduleOptList.isPresent()) {
      return Lists.newArrayList();
    }
    return postScheduleOptList.get();
  }

  /**
   * <b>Create Notification</b>
   * @param post
   * @param task
   * @param meaages
   * @param status
   * @param description
   */
  private void createAlert(Post post, String task, String meaages, int status, String description) {
    try {
      Alert notofication = new Alert();
      notofication.setUserId(post.getCreatedBy());
      notofication.setUpdatedBy(post.getCreatedBy());
      notofication.setUpdatedBy(post.getCreatedBy());
      notofication.setNotificationType(task);
      notofication.setMessage(meaages);
      notofication.setDescription(String.format(description,post.getId()));
      notofication.setStatus(status);
      notofication.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      notofication.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      notificationService.save(notofication);
    } catch (Exception e) {
      log.error("Error while saving alert", e);
      throw new SocioSeerException("Error while saving alert.");
    }
  }

}
