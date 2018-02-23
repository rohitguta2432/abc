package com.socioseer.restapp.service.impl;

import static com.socioseer.common.domain.SocialHandler.OBJECT_KEY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.hibernate.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.NotificationMeaage;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.EMAIL_TYPE;
import com.socioseer.common.domain.EmailNotification;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Alert;
import com.socioseer.common.domain.model.campaign.SocialPlatform;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.cache.Cache;
import com.socioseer.restapp.dao.api.SocialHandlerDao;
import com.socioseer.restapp.service.api.AlertService;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.api.SocialHandlerService;
import com.socioseer.restapp.service.api.SocialPlatformService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.email.EmailService;
import com.socioseer.restapp.service.pubsub.SocioSeerKafkaProducer;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * <h3>SocialHandler Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class SocialHandlerServiceImpl implements SocialHandlerService {

  @Autowired
  private SocialHandlerDao socialHandlerDao;

  @Autowired
  private ClientService clientService;

  @Autowired
  private SocialPlatformService socialPlatformService;

  @Autowired
  private Cache cache;

  @Autowired
  private UserService userService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Value("${facebook.token.expiration}")
  private int expirationTime;

  final static String USER_ID = "userId";
  final static String STATUS = "status";

  @Autowired
  private SocioSeerKafkaProducer<Long> sociSeerKafkaProducer;

  @Autowired
  private AlertService notificationService;

  @Value("${kafka.topic.socialHandlerUserId}")
  private String socialHandlerUserTopic;

  @Value("${alert.token.email.subject}")
  private String emailSubject;

  @Value("${alert.token.email.from}")
  private String emailFrom;

  @Value("${alert.token.email.message}")
  private String emailMessage;

  @Value("${alert.token.email.page}")
  private String htmlPage;

  private static final Long ONE_DAY = 86400000L;

  @Autowired
  private EmailService emailService;

  /**
   * <b>Save SocialHandler</b>
   * 
   * @param socialHandler
   * @return returns SocialHandler
   */
  @Override
  public SocialHandler save(@NonNull SocialHandler socialHandler) {
    validateSocialHandler(socialHandler);
    try {
      SocialPlatform socialPlatform = getSocialPlatform(socialHandler.getPlatformId());
      socialHandler.setSocialPlatform(socialPlatform);
      socialHandler.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      socialHandler.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      socialHandler = socialHandlerDao.save(socialHandler);
      cache.put(OBJECT_KEY, socialHandler.getId(), socialHandler);
      if (PlatformConstant.TWITTER.equalsIgnoreCase(socialHandler.getSocialPlatform().getName())) {
        sociSeerKafkaProducer.produce(socialHandlerUserTopic,
            Long.valueOf(socialHandler.getAccessToken().get(ModelConstants.TWITTER_USER_ID)));
      }
      return socialHandler;
    } catch (CacheException ce) {
      String message = "Error while saving social handler to cache";
      socialHandlerDao.delete(socialHandler.getId());
      throw new SocioSeerException(message);
    } catch (Exception e) {
      String message = String.format("Error while saving social handler for client id : %s",
          socialHandler.getClientId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update SocialHandler</b>
   * 
   * @param iD
   * @param socialHandler
   * @return returns SocialHandler
   */
  @Override
  public SocialHandler update(@NonNull String id, @NonNull SocialHandler socialHandler) {
    validateSocialHandler(socialHandler);
    SocialHandler existingSocialHandler = get(id);
    if (ObjectUtils.isEmpty(existingSocialHandler)) {
      String message = String.format("No social handler found with id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    SocialPlatform socialPlatform = getSocialPlatform(socialHandler.getPlatformId());
    try {
      socialHandler.setSocialPlatform(socialPlatform);
      socialHandler.setId(existingSocialHandler.getId());
      socialHandler.setCreatedDate(existingSocialHandler.getCreatedDate());
      socialHandler.setCreatedBy(existingSocialHandler.getCreatedBy());
      socialHandler.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      socialHandler = socialHandlerDao.save(socialHandler);
      cache.put(OBJECT_KEY, socialHandler.getId(), socialHandler);
      return socialHandler;
    } catch (CacheException ce) {
      String message = "Error while saving social handler to cache";
      socialHandlerDao.delete(socialHandler.getId());
      throw new SocioSeerException(message);
    } catch (Exception e) {
      String message = String.format("Error while updating social handler for with id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get SocialHandler by Id</b>
   * 
   * @param id
   * @return returns SocialHandler
   */
  @Override
  public SocialHandler get(@NonNull String id) {
    try {
      Optional<SocialHandler> socialHandlerOptional =
          cache.get(OBJECT_KEY, id, SocialHandler.class);
      if (socialHandlerOptional.isPresent()) {
        return socialHandlerOptional.get();
      }
      return null;
    } catch (CacheException e) {
      String message = String.format("Error while fetching social handler from cache : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Save SocialHandlers</b>
   * 
   * @param socialHandlers
   * @return returns List of SocialHandlers
   */
  @Override
  public List<SocialHandler> save(@NonNull List<SocialHandler> socialHandlers) {
    if (CollectionUtils.isEmpty(socialHandlers)) {
      log.info("Social handlers list cannot be empty/null.");
      throw new IllegalArgumentException("Social handlers list cannot be empty/null.");
    }

    List<SocialHandler> newSocialHandlers = new ArrayList<SocialHandler>();
    socialHandlers.forEach(socialHandler -> {
      List<SocialHandler> socialHandles =
          getSocialHandlerByClientId(socialHandler.getClientId(), null);
      for (SocialHandler social : socialHandles) {
        if (social.getAccessToken().get(USER_ID)
            .equals(socialHandler.getAccessToken().get(USER_ID))) {
          socialHandler.setId(social.getId());
        }
      }
      newSocialHandlers.add(socialHandler);
    });
    socialHandlers = newSocialHandlers;
    socialHandlers.stream().forEach(socialHandler -> {
      validateSocialHandler(socialHandler);
      socialHandler.setSocialPlatform(getSocialPlatform(socialHandler.getPlatformId()));
    });
    try {
      socialHandlers = (List<SocialHandler>) socialHandlerDao.save(socialHandlers);
      Map<String, SocialHandler> socialHandlerMap = getSocialHandlerIdMap(socialHandlers);
      cache.multiPut(OBJECT_KEY, socialHandlerMap);
      return socialHandlers;
    } catch (CacheException ce) {
      socialHandlerDao.delete(socialHandlers);
      String message = String.format("Error while saving social handlers into cache");
      log.error(message, ce);
      throw new SocioSeerException(message);
    } catch (Exception ex) {
      String message = String.format("Error while saving social handlers");
      log.error(message, ex);
      throw new SocioSeerException(message);
    }

  }

  /**
   * <b>Get SocialHandler by clientId</b>
   * 
   * @param clientId
   * @param pageable
   * @return returns List of SocialHandler
   */
  @Override
  public List<SocialHandler> getSocialHandlerByClientId(@NonNull String clientId,
      Pageable pageable) {
    try {
      Query query = QueryBuilder.createQuery(null, pageable);
      query.addCriteria(
          new Criteria().andOperator(Criteria.where(ModelConstants.CLIENT_ID).is(clientId),
              Criteria.where(STATUS).ne(StatusConstants.DELETED)));
      List<SocialHandler> socialHandlers = mongoTemplate.find(query, SocialHandler.class);
      return socialHandlers;
    } catch (Exception e) {
      String message =
          String.format("Error while fetching social handlers for client id  : %s", clientId);
      log.info(message);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get SocialHandlers</b>
   * 
   * @return returns List of SocialHandlers
   */
  @Override
  public List<SocialHandler> getSocialHandlers() {
    try {
      List<SocialHandler> socialHandlerList = new ArrayList<SocialHandler>();
      socialHandlerDao.findAll().forEach(socialHandlerList::add);
      return socialHandlerList;
    } catch (Exception ex) {
      String message = "Error fetching all social handlers";
      log.error(message, ex);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update Last Fetched SocialHandler</b>
   * 
   * @param data
   */
  @Override
  public void updateHandlerLastFetch(@NonNull Map<String, String> data) {
    String handlerId = data.get(ModelConstants.HANDLER_ID).toString();

    SocialHandler handler = get(handlerId);
    if (handler == null) {
      String message = String.format("No social handle found with id : %s", handlerId);
      log.error(message);
      return;
    }

    String platformType = data.get(ModelConstants.PLATFORM_TYPE);

    if (PlatformConstant.TWITTER.equalsIgnoreCase(platformType)) {
      populateTwitterLastFetchId(data, handler);
    } else if (PlatformConstant.FACEBOOK.equalsIgnoreCase(platformType)) {
      populateFacebookLastFetchTimeStamp(data, handler);
    }

    handler.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
    socialHandlerDao.save(handler);
    // updating cache
    cache.put(SocialHandler.OBJECT_KEY, handlerId, handler);
  }

  /**
   * <b>Validate Facebook Handler Tokens</b>
   */
  @Override
  public void validateFacebookHandlerTokens() {
    long expirationTimeInMillis = TimeUnit.DAYS.toMillis(expirationTime);
    long expirationBeforeSevenDays = expirationTimeInMillis - TimeUnit.DAYS.toMillis(7);
    long expirationBeforeThreeDays = expirationTimeInMillis - TimeUnit.DAYS.toMillis(3);
    SocialPlatform socialPlatform =
        socialPlatformService.getPlatformByName(PlatformConstant.FACEBOOK.toLowerCase());
    if (!ObjectUtils.isEmpty(socialPlatform)) {
      List<SocialHandler> socialHandlers = socialHandlerDao.findAllBySocialPlatform(socialPlatform);
      if (!CollectionUtils.isEmpty(socialHandlers)) {
        socialHandlers.forEach(socialHandler -> {
          long updatedSevenDays = socialHandler.getUpdatedDate() + expirationBeforeSevenDays;
          long updatedThreeDays = socialHandler.getUpdatedDate() + expirationBeforeThreeDays;
          long currentDate = DateUtil.getCurrentTimeInMilliseconds();
          if (currentDate - updatedSevenDays == TimeUnit.DAYS.toMillis(7)) {
            sevenDaysAlert(socialHandler.getCreatedBy(), updatedSevenDays - currentDate);
          }
          if (currentDate - updatedThreeDays == TimeUnit.DAYS.toMillis(3)) {
            threeDaysAlert(socialHandler, currentDate - updatedThreeDays);
          }
        });
      }
    }
  }

  /**
   * <b>Get All SocialHandlers</b>
   * 
   * @param pageable
   * @param filters
   * @return returns List of SocialHandlers
   */
  @Override
  public List<SocialHandler> getAll(Pageable pageable, List<Filter> filters) {
    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      return mongoTemplate.find(query, SocialHandler.class);
    } catch (Exception e) {
      log.error("Error while fetching social handler.", e);
      throw new SocioSeerException("Error while fetching social handler.");
    }
  }

  /**
   * <b>Get SocialHandler by clientId and socialPlatform</b>
   * 
   * @param clientId
   * @param socialPlatform
   * @return returns List of SocialHandler
   */
  @Override
  public List<SocialHandler> getSocialHandlerByClientIdAndSocialPlatform(@NonNull String clientId,
      @NonNull SocialPlatform socialPlatform) {
    return socialHandlerDao.findAllByClientIdAndSocialPlatform(clientId, socialPlatform);
  }

  /**
   * <b>Get SocialHandler by clientId and postId</b>
   * 
   * @param clientId
   * @param platformId
   * @param pageable
   * @return returns List of SocialHandler
   */
  @Override
  public List<SocialHandler> getSocialHandlerByClientIdAndPostId(@NonNull String clientId,
      @NonNull String platformId, Pageable pageable) {
    try {
      SocialPlatform socialPlatform = socialPlatformService.get(platformId);
      if (ObjectUtils.isEmpty(socialPlatform)) {
        String message = String.format("Social platform id not extsted %s", platformId);
        log.info(message);
        throw new IllegalArgumentException(message);
      }
      List<SocialHandler> socialHandlers =
          socialHandlerDao.findAllByClientIdAndSocialPlatform(clientId, socialPlatform);
      return socialHandlers.stream().filter(socialHandler -> socialHandler.getStatus() != 3)
          .collect(Collectors.toList());

    } catch (Exception e) {
      log.error("Error while fetching social handler.", e);
      throw new SocioSeerException("Error while fetching social handler.");
    }

  }

  /**
   * <b>Delete SocialHandler</b>
   * 
   * @param id
   * @param updatedBy
   * 
   */
  @Override
  public void delete(@NonNull String id, String updatedBy) {
    try {
      SocialHandler socialHandler = socialHandlerDao.findOne(id);
      validateDelete(socialHandler, id);
      socialHandler.setStatus(StatusConstants.DELETED);
      socialHandler.setUpdatedBy(updatedBy);
      socialHandler.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      cache.delete(SocioSeerConstant.DEFAULT_SOCIAL_HANDALER_KEY, socialHandler.getId());
      socialHandlerDao.save(socialHandler);
    } catch (Exception e) {
      String message = String.format("Error while deleting social handler $s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Populate Facebook Last Fetch TimeStamp</b>
   * 
   * @param data
   * @param handler
   */
  private void populateFacebookLastFetchTimeStamp(Map<String, String> data, SocialHandler handler) {
    handler.getAccessToken().put(ModelConstants.FB_POSTS_SINCE,
        data.get(ModelConstants.FB_POSTS_SINCE));
    handler.getAccessToken().put(ModelConstants.FB_TAGGED_SINCE,
        data.get(ModelConstants.FB_TAGGED_SINCE));
  }

  /**
   * <b>Populate Twitter Last Fetch Id</b>
   * 
   * @param data
   * @param handler
   */
  private void populateTwitterLastFetchId(Map<String, String> data, SocialHandler handler) {
    handler.getAccessToken().put(ModelConstants.LAST_FAVORITE_TWEET_ID,
        data.get(ModelConstants.LAST_FAVORITE_TWEET_ID));
    handler.getAccessToken().put(ModelConstants.LAST_USER_TIMELINE_TWEET_ID,
        data.get(ModelConstants.LAST_USER_TIMELINE_TWEET_ID));
    handler.getAccessToken().put(ModelConstants.LAST_MENTION_TIMELINE_TWEET_ID,
        data.get(ModelConstants.LAST_MENTION_TIMELINE_TWEET_ID));
  }

  /**
   * <b>Validate SocialHandler</b>
   * 
   * @param socialHandler
   */
  private void validateSocialHandler(SocialHandler socialHandler) {

    if (StringUtils.isEmpty(socialHandler.getClientId())) {
      log.info("Client id can not be null/empty.");
      throw new IllegalArgumentException("Client id can not be null/empty.");
    }

    if (StringUtils.isEmpty(socialHandler.getCreatedBy())) {
      log.info("Created by id can not be null/empty.");
      throw new IllegalArgumentException("Created by id can not be null/empty.");
    }

    if (CollectionUtils.isEmpty(socialHandler.getAccessToken())) {
      log.info("Access token can not be null/empty.");
      throw new IllegalArgumentException("Access token can not be null/empty.");
    }

    if (ObjectUtils.isEmpty(clientService.get(socialHandler.getClientId()))) {
      String message = String.format("No client found with id : %s", socialHandler.getClientId());
      log.info(message);
      throw new IllegalArgumentException(message);
    }

  }

  /**
   * <b>Get SocialPlatform by plateformId</b>
   * 
   * @param platformId
   * @return returns SocialPlatform
   */
  private SocialPlatform getSocialPlatform(String platformId) {
    if (StringUtils.isEmpty(platformId)) {
      log.info("Social platform id can not be null/empty.");
      throw new IllegalArgumentException("Social platform can not be null/empty.");
    }

    SocialPlatform socialPlatform = socialPlatformService.get(platformId);
    if (ObjectUtils.isEmpty(socialPlatform)) {
      log.info("social platform is invalid for platform id " + platformId);
      throw new IllegalArgumentException("Access token can not be null/empty.");
    }
    return socialPlatform;
  }

  /**
   * <b>Get SocialHandlers Id</b>
   * 
   * @param socialHandlers
   * @return
   */
  private Map<String, SocialHandler> getSocialHandlerIdMap(List<SocialHandler> socialHandlers) {
    Map<String, SocialHandler> socialHandlerMap = new HashMap<String, SocialHandler>();
    for (SocialHandler socialHandler : socialHandlers) {
      if (!socialHandlerMap.containsKey(socialHandler.getId())) {
        socialHandlerMap.put(socialHandler.getId(), socialHandler);
      }
    }
    return socialHandlerMap;
  }

  /**
   * <b>Validate SocialHandler for Delete</b>
   * 
   * @param socialHandler
   * @param id
   */
  private void validateDelete(SocialHandler socialHandler, String id) {
    if (ObjectUtils.isEmpty(socialHandler)) {
      String message = String.format("Error socialHandler not find by user id : %s", id);
      log.error(message);
      throw new IllegalArgumentException(message);
    }
    if (socialHandler.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error socialHandler already deleted");
      log.error(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * <b>Create Alert</b>
   * 
   * @param userId
   * @param task
   * @param meaages
   * @param status
   * @param description
   */
  private void createAlert(String userId, String task, String meaages, int status,
      String description) {
    try {
      Alert notofication = new Alert();
      notofication.setUpdatedBy(userId);
      notofication.setUpdatedBy(userId);
      notofication.setUserId(userId);
      notofication.setNotificationType(task);
      notofication.setMessage(meaages);
      notofication.setDescription(description);
      notofication.setStatus(status);
      notofication.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      notofication.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      notificationService.save(notofication);
    } catch (Exception e) {
      log.error("Error while saving alert", e);
      throw new SocioSeerException("Error while saving alert.");
    }
  }

  /**
   * <b>Create Alert for week</b>
   * 
   * @param userId
   * @param days
   */
  private void sevenDaysAlert(String userId, long days) {
    createAlert(userId, NotificationMeaage.MESSAGE_FOR_FB_NOTIFICATION,
        String.format(NotificationMeaage.DESCRIPTION_FOR_FB_NOTIFICATION,
            Math.round(days / ONE_DAY)),
        StatusConstants.NOTIFICATION_NOT_VIEWED, String.format(
            NotificationMeaage.DESCRIPTION_FOR_FB_NOTIFICATION, Math.round(days / ONE_DAY)));
  }

  /**
   * <b>Create Alert for Three Days</b>
   * 
   * @param socialHandler
   * @param days
   */
  private void threeDaysAlert(SocialHandler socialHandler, long days) {
    User user = userService.get(socialHandler.getCreatedBy());
    EmailNotification emailNotification = EmailNotification.builder()
        .toList(Arrays.asList(user.getEmail())).subject(emailSubject).from(emailFrom)
        .message(String.format(emailMessage, Math.round(days / ONE_DAY))).build();
    List<String> emails = Arrays.asList(user.getEmail());
    emailService.sendEmail(EMAIL_TYPE.ACCESS_TOKEN_ALERT, emailNotification, emails);
    createAlert(socialHandler.getCreatedBy(), NotificationMeaage.MESSAGE_FOR_FB_NOTIFICATION,
        String.format(NotificationMeaage.DESCRIPTION_FOR_FB_NOTIFICATION,
            Math.round(days / ONE_DAY)),
        StatusConstants.NOTIFICATION_NOT_VIEWED, String.format(
            NotificationMeaage.DESCRIPTION_FOR_FB_NOTIFICATION, Math.round(days / ONE_DAY)));
  }

  /**
   * <b>Get SocialHandler by SocialPlateform</b>
   * 
   * @param socialPlatform
   * @return returns List of SocialHandler
   */
  @Override
  public List<SocialHandler> findBySocialPlatform(@NonNull SocialPlatform socialPlatform) {
    return socialHandlerDao.findBySocialPlatform(socialPlatform);
  }

}
