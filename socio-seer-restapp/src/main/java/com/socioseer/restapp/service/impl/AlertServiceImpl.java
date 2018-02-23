package com.socioseer.restapp.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.ImmutableMap;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Alert;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.AlertDao;
import com.socioseer.restapp.dao.api.CampaignDao;
import com.socioseer.restapp.service.api.AlertService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>AlertService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class AlertServiceImpl implements AlertService {

  @Autowired
  private AlertDao alertDao;

  @Autowired
  private CampaignDao campaignDao;

  @Autowired
  private UserService userService;

  @Autowired
  private MongoTemplate mongoTemplate;

  final static String USER_ID = "userId";

  /**
   * <b>Valkidate Facebook Token for Alert </b>
   * @param	 userId	
   * @return returns Map<String,String>
   */
  @Override
  public Map<String,String> validateFacebookToken(@NonNull String userId){
    Set<Campaign> campaignSet = new HashSet<Campaign>();
    Set<SocialHandler> socialHandlerSet = new HashSet<SocialHandler>();
      Map<String,String> map = new HashMap<String,String>();
    try{
      User user = userService.get(userId); 
      user.getTeams().forEach(team -> {
        if (!ObjectUtils.isEmpty(team)){
          List<Campaign> campaignList = campaignDao.findByTeam(team);
          campaignSet.addAll(campaignList);
        }
      });
      campaignSet.forEach(campaign -> {
        if (!ObjectUtils.isEmpty(campaign)){
          List<SocialHandler> socialHandlerList = campaign.getHandles();
          socialHandlerSet.addAll(socialHandlerList);
        }
      });
      
      socialHandlerSet.forEach(socialHandler ->{
       long createdDate = socialHandler.getUpdatedDate();
       long currentDate = DateUtil.getCurrentTimeInMilliseconds();
       if(!(createdDate + 55*86400000 > currentDate)){
         //map.put(socialHandler.getAccessToken().get("screen_name"), "Token is Valid");
       }else if(currentDate > (createdDate + 55*86400000)  && currentDate < (createdDate + 60*86400000) ){
         map.put(socialHandler.getAccessToken().get("screen_name"), "Token is going to expire");
       }else if(currentDate > (createdDate + 60*86400000) ) {
         map.put(socialHandler.getAccessToken().get("screen_name"), "Token is expired");
       }
      });
      
    }catch(Exception e){
      log.error("Error while validating facebook Token.", e);
        throw new SocioSeerException("Error while validating facebook token.");
    }
    return map;
  }

  /**
   * <b>Save Alert </b>
   * @param	 alert	Alert Object	
   * @return returns Alert Object
   */
  @Override
  public Alert save(@NonNull Alert alert) {

    validateNotification(alert);
    try {
      alert.setStatus(StatusConstants.NOTIFICATION_NOT_VIEWED);
      alert.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      alert.setUpdatedDate(alert.getCreatedDate());
      return alertDao.save(alert);
    } catch (Exception e) {
      String message = String.format("Error while saving alert for user : %s", alert.getUserId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update Alert </b>
   * @param id
   * @param	alert
   * @return returns Alert Object
   */
  @Override
  public Alert update(@NonNull String id, @NonNull Alert alert) {

    try {
      Alert existingNotification = alertDao.findOne(id);
      if (ObjectUtils.isEmpty(existingNotification)) {
        String message = String.format("No alert found with id : %s", id);
        log.info(message);
        throw new IllegalArgumentException(message);
      }
      existingNotification.setStatus(alert.getStatus());
      existingNotification.setCreatedDate(existingNotification.getCreatedDate());
      existingNotification.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      alertDao.save(existingNotification);

    } catch (Exception e) {
      String message = String.format("Error while updating alert with id : %s", alert.getUserId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }

    return null;
  }

  /**
   * <b>Get Alert by id</b>
   * @param		id
   * @return	returns Alert
   */
  @Override
  public Alert get(@NonNull String id) {

    try {
      return alertDao.findOne(id);
    } catch (Exception e) {
      String message = String.format("Error while fetching alert with id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get Alert counts by userId</b>
   * @param		userId	
   * @return	returns integer
   */
  @Override
  public int getCountByUserId(@NonNull String userId) {

    try {
      List<Alert> existingNotifications =
          alertDao.findByUserIdAndStatus(userId, StatusConstants.NOTIFICATION_NOT_VIEWED);
      Map<String, Integer> alertMap = countNotification(existingNotifications);
      return alertMap.size();
    } catch (Exception e) {
      String message = String.format("Error while fetching alert count for user id : %s", userId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get Alert list by userId</b>
   * @param		userId
   * @param		pageable
   * @param		filters
   * @return	returns Alert list
   */
  @Override
  public List<Alert> getNotificationByUserId(@NonNull String userId, Pageable pageable,
      List<Filter> filters) {
    try {

      List<Alert> nitifications =
          mongoTemplate.find(
              QueryBuilder.createQuery(filters, ImmutableMap.of(USER_ID, userId), pageable),
              Alert.class);
      return nitifications;
    } catch (Exception e) {
      String message = String.format("Error while fetching alert for user id : %s", userId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update Alert status by userId</b>
   * @param		userId
   * @return	returns boolean
   */
  @Override
  public boolean updateNotificationStatus(@NonNull String userId) {

    try {
      final long updatedDate = DateUtil.getCurrentTimeInMilliseconds();
      List<Alert> existingNotifications =
          alertDao.findByUserIdAndStatus(userId, StatusConstants.NOTIFICATION_NOT_VIEWED);
      existingNotifications.forEach(alert -> {
        alert.setStatus(StatusConstants.NOTIFICATION_VIEWED);
        alert.setUpdatedDate(updatedDate);
        alertDao.save(alert);
      });
    } catch (Exception e) {
      String message =
          String.format("Error occured while updating alert status to : %d for user : %s",
              StatusConstants.NOTIFICATION_VIEWED, userId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
    return true;
  }

  /**
   * <b>Validate Alert</b>
   * @param alert
   */
  private void validateNotification(Alert alert) {

    if (StringUtils.isEmpty(alert.getUserId())) {
      log.info("User id cannot be null/empty.");
      throw new IllegalArgumentException("User id cannot be null/empty.");
    }

    if (StringUtils.isEmpty(alert.getNotificationType())) {
      log.info("Alert type cannot be null/empty.");
      throw new IllegalArgumentException("Alert type cannot be null/empty.");
    }
    if (ObjectUtils.isEmpty(userService.get(alert.getUserId()))) {
      String message = String.format("User id not found by user id %s", alert.getUserId());
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * <b>Delete Alert by id and deletedBy</b>
   * @param		id
   * @param		updatedBy
   */
  @Override
  public void delete(String id, String updatedBy) {

    try {
      Alert alert = alertDao.findOne(id);
      validateNotificationDelete(alert, id);
      alert.setStatus(StatusConstants.DELETED);
      alert.setUpdatedBy(updatedBy);
      alert.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      alert = alertDao.save(alert);
      String message = String.format("Alert deleted by alert id %s", updatedBy);
      log.info(message);
    } catch (Exception e) {
      String message = String.format("Error while fetching alert by alert id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate Alert</b>
   * @param alert
   * @param id
   */
  private void validateNotificationDelete(Alert alert, @NonNull String id) {
    if (ObjectUtils.isEmpty(alert)) {
      String message = String.format("Error alert not find by alert id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    if (alert.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error role already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * <b>Get Alert list</b>
   * @param		pageable
   * @param		filters
   * @return	returns Alert list
   */
  @Override
  public List<Alert> getAll(Pageable pageable, List<Filter> filters) {
    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      return mongoTemplate.find(query, Alert.class);
    } catch (Exception e) {
      log.error("Error while fetching alerts.", e);
      throw new SocioSeerException("Error while fetching alerts.");
    }
  }

  /**
   * <b>Count Alert</b>
   * @param alerts	alert objects list
   * @return	returns map object
   */
  private Map<String, Integer> countNotification(List<Alert> alerts) {
    Map<String, Integer> notoficationMap = new HashMap<String, Integer>();
    if (!CollectionUtils.isEmpty(alerts)) {
      alerts.forEach(alert -> {
        int count = 0;

        if (alert.getNotificationType().equals(ModelConstants.NOTIFICATION_TASK)) {
          if (ObjectUtils.isEmpty(notoficationMap.get(ModelConstants.NOTIFICATION_TASK))) {
            notoficationMap.put(ModelConstants.NOTIFICATION_TASK, 1);
          } else {
            count = (int) notoficationMap.get(ModelConstants.NOTIFICATION_TASK);
            notoficationMap.put(ModelConstants.NOTIFICATION_TASK, ++count);
          }
        } else if (alert.getNotificationType().equals(ModelConstants.NOTIFICATION_EVENT)) {

          if (ObjectUtils.isEmpty(notoficationMap.get(ModelConstants.NOTIFICATION_EVENT))) {
            notoficationMap.put(ModelConstants.NOTIFICATION_EVENT, 1);
          } else {
            count = (int) notoficationMap.get(ModelConstants.NOTIFICATION_EVENT);
            notoficationMap.put(ModelConstants.NOTIFICATION_EVENT, ++count);
          }

        } else if (alert.getNotificationType().equals(ModelConstants.NOTIFICATION_DUE_DATE)) {

          if (ObjectUtils.isEmpty(notoficationMap.get(ModelConstants.NOTIFICATION_DUE_DATE))) {
            notoficationMap.put(ModelConstants.NOTIFICATION_DUE_DATE, 1);
          } else {
            count = (int) notoficationMap.get(ModelConstants.NOTIFICATION_DUE_DATE);
            notoficationMap.put(ModelConstants.NOTIFICATION_DUE_DATE, ++count);
          }

        } else if (alert.getNotificationType().equals(ModelConstants.NOTIFICATION_MATRIX)) {
          if (ObjectUtils.isEmpty(notoficationMap.get(ModelConstants.NOTIFICATION_MATRIX))) {
            notoficationMap.put(ModelConstants.NOTIFICATION_MATRIX, 1);
          } else {
            count = (int) notoficationMap.get(ModelConstants.NOTIFICATION_MATRIX);
            notoficationMap.put(ModelConstants.NOTIFICATION_MATRIX, ++count);
          }
        } else if (alert.getNotificationType().equals(ModelConstants.NOTIFICATION_TEAM)) {
          if (ObjectUtils.isEmpty(notoficationMap.get(ModelConstants.NOTIFICATION_TEAM))) {
            notoficationMap.put(ModelConstants.NOTIFICATION_TEAM, 1);
          } else {
            count = (int) notoficationMap.get(ModelConstants.NOTIFICATION_TEAM);
            notoficationMap.put(ModelConstants.NOTIFICATION_TEAM, ++count);
          }
        }

      });
    }
    return notoficationMap;
  }

  /**
   * <b>Get All Not viewed alerts</b>
   * @param		userId
   * @param		pageable
   * @return	returns map object
   */
  @Override
  public Map<String, Integer> getAllNotViewed(@NonNull String userId, Pageable pageable) {
    try {
      List<Alert> existingNotifications =
          alertDao.findByUserIdAndStatus(userId, StatusConstants.NOTIFICATION_NOT_VIEWED);
      Map<String, Integer> alertMap = countNotification(existingNotifications);
      return alertMap;
    } catch (Exception e) {
      log.error("Error while fetching alerts.", e);
      throw new SocioSeerException("Error while fetching alerts.");
    }
  }
}
