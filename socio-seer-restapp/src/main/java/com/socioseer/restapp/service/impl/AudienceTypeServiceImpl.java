package com.socioseer.restapp.service.impl;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.model.AudienceType;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.AudienceTypeDao;
import com.socioseer.restapp.service.api.AudienceTypeService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>AudienceTypeService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class AudienceTypeServiceImpl implements AudienceTypeService {

  @Autowired
  private AudienceTypeDao audienceTypeDao;
  
  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * <b>Save AudienceType</b>
   * @param		audienceType
   * @return	returns AudienceType
   */
  @Override
  public AudienceType save(@NonNull AudienceType audienceType) {
    validateAudienceType(audienceType);
    try {
      audienceType.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      audienceType.setUpdatedDate(audienceType.getCreatedDate());
      return audienceTypeDao.save(audienceType);
    } catch (Exception e) {
      String message =
          String.format("Error while creating an audience type with type : %s",
              audienceType.getType());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update AudienceType</b>
   * @param		id
   * @param		audienceType
   * @return	returns AudienceType
   */
  @Override
  public AudienceType update(@NonNull String id, @NonNull AudienceType audienceType) {
    validateAudienceType(audienceType);
    AudienceType existingAudienceType = audienceTypeDao.findOne(id);
    if (ObjectUtils.isEmpty(existingAudienceType)) {
      String message = String.format("No audience type found with id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    try {
      audienceType.setId(id);
      audienceType.setCreatedDate(existingAudienceType.getCreatedDate());
      audienceType.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return audienceTypeDao.save(audienceType);
    } catch (Exception e) {
      String message = String.format("Error while updating audience type with id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get AudienceType by id</b>
   * @param		id
   * @return	returns AudienceType
   */
  @Override
  public AudienceType get(@NonNull String id) {
    try {
      return audienceTypeDao.findOne(id);
    } catch (Exception e) {
      String message = String.format("Error while fetching audience type with id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate AudienceType</b>
   * @param entity
   */
  private void validateAudienceType(AudienceType entity) {

    if (StringUtils.isEmpty(entity.getType())) {
      log.info("Audience type cannot be empty/null");
      throw new IllegalArgumentException("Audience type cannot be empty/null");
    }
  }

  /**
   * <b>Get all AudienceTypes</b>
   * @param		pageable
   * @param		filters
   * @return	returns AudienceType list
   */
  @Override
  public List<AudienceType> getAllAudienceTypes(Pageable pageable, List<Filter> filters) {

    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      List<AudienceType> clients = mongoTemplate.find(query, AudienceType.class);
      return clients;
    } catch (Exception e) {
      log.error("Error while fetching audience types", e);
      throw new SocioSeerException("Error while fetching audience types");
    }
  }
 
  /**
   * <b>Delete AudienceType by id</b>
   * @param		id
   * @param		updatedBy
   */
  @Override
  public void delete(String id ,String updatedBy) {

    try {
      AudienceType audienceType = audienceTypeDao.findOne(id);
      validateAudienceTypeDelete(audienceType,updatedBy);
      audienceType.setStatus(StatusConstants.DELETED);
      audienceType.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      audienceType = audienceTypeDao.save(audienceType);
      String message = String.format("AudienceType deleted by audienceType id %s", updatedBy);
      log.info(message);
    } catch (Exception e) {
      String message =
          String.format("Error while fetching audienceType by audienceType id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  
  /**
   * <b>Validate AudienceType for Delete</b>
   * @param audienceType
   * @param id
   */
  private void validateAudienceTypeDelete(AudienceType audienceType, String id) {
    if (ObjectUtils.isEmpty(audienceType)) {
      String message = String.format("Error audienceType not found by audienceType id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    if (audienceType.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error role already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }
}
