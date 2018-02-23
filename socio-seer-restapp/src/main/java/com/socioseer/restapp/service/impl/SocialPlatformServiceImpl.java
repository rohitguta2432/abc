package com.socioseer.restapp.service.impl;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.domain.model.campaign.SocialPlatform;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.SocialPlatformDao;
import com.socioseer.restapp.service.api.SocialPlatformService;
import com.socioseer.restapp.service.util.DateUtil;

/**
 * <h3>SocialPlatform Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class SocialPlatformServiceImpl implements SocialPlatformService {

  @Autowired
  private SocialPlatformDao socialPlatformDao;

  /**
   * <b>Save SocialPlatform</b>
   * @param socialPlatform
   * @return returns SocialPlatform
   */
  @Override
  public SocialPlatform save(@NonNull SocialPlatform socialPlatform) {
    validatePlatform(socialPlatform);
    checkDuplicate(socialPlatform.getName());
    try {
      socialPlatform.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      socialPlatform.setUpdatedDate(socialPlatform.getCreatedDate());
      return socialPlatformDao.save(socialPlatform);
    } catch (Exception e) {
      String message =
          String.format("Error while saving socialPlatform for with  name : %s",
              socialPlatform.getName());
      log.error(message, e);
      throw new SocioSeerException(message);
    }

  }
  
  /**
   * <b>Update SocialPlatform</b>
   * @param socialPlatformId
   * @param socialPlatform
   * @return returns SocialPlatform  
   */
  @Override
  public SocialPlatform update(@NonNull String socialPlatformId,
      @NonNull SocialPlatform socialPlatform) {

    validatePlatform(socialPlatform);
    SocialPlatform existingSocialPlatform = socialPlatformDao.findOne(socialPlatformId);

    if (ObjectUtils.isEmpty(existingSocialPlatform)) {
      String message =
          String.format("No socialPlatform found with socialPlatform id : %s", socialPlatformId);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    try {
      socialPlatform.setId(socialPlatformId);
      socialPlatform.setCreatedDate(existingSocialPlatform.getCreatedDate());
      socialPlatform.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return socialPlatformDao.save(socialPlatform);
    } catch (Exception e) {
      String message =
          String.format("Error while updating socialPlatform by socialPlatform id : %s",
              socialPlatformId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  /**
   * <b>Get SocialPlatform by Id</b>
   * @param socialPlatformId
   * @return returns SocialPlatform
   */
  @Override
  public SocialPlatform get(@NonNull String socialPlatformId) {
    try {
      return socialPlatformDao.findOne(socialPlatformId);
    } catch (Exception e) {
      String message =
          String.format("Error while fetching socialPlatform by socialPlatform id : %s",
              socialPlatformId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  /**
   * <b>Get SocialPlatform by Name</b>
   * @param platformName
   * @return returns SocialPlatform
   */
  @Override
  public SocialPlatform getPlatformByName(@NonNull String platformName) {
    try {
      return socialPlatformDao.findOneByName(platformName);
    } catch (Exception e) {
      String message =
          String
              .format("Error while fetching socialPlatform by getplatformName : %s", platformName);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  /**
   * <b>Get All SocialPlatforms</b>
   * @return returns List of SocialPlatforms
   */
  @Override
  public List<SocialPlatform> getAllPlatforms() {
    try {
      return socialPlatformDao.findAll();
    } catch (Exception e) {
      String message = String.format("Error while fetching all socialPlatform");
      log.error(message, e);
      throw new SocioSeerException(message);
    }

  }
/**
 * <b>Validate Plateform</b>
 * @param socialPlatform
 */
  private void validatePlatform(SocialPlatform socialPlatform) {
    if (StringUtils.isEmpty(socialPlatform.getName())) {
      log.info("SocialPlatform plateform can not be empty/null.");
      throw new IllegalArgumentException("SocialPlatform plateform can not be empty/null.");
    }
  }

  /**
   * <b>Check Duplicate for Plateform</b>
   * @param platformName
   */
  private void checkDuplicate(String platformName) {
    SocialPlatform existingSocialPlatform = socialPlatformDao.findOneByName(platformName);
    if (ObjectUtils.isEmpty(existingSocialPlatform)) {
      log.info("SocialPlatform plateform can not be douplicat plateformName :%s" + platformName);
      throw new IllegalArgumentException("SocialPlatform plateform can not be douplicat  : %s"
          + platformName);
    }
  }

}
