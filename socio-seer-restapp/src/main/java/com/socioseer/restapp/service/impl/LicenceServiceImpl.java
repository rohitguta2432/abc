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
import com.socioseer.common.domain.model.Licence;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.LicenceDao;
import com.socioseer.restapp.service.api.LicenceService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>LicenceService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class LicenceServiceImpl implements LicenceService {

  @Autowired
  private LicenceDao licenceDao;

  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * <b>Save Licence</b>
   * @param licence
   * @return	returns Licence
   */
  @Override
  public Licence save(@NonNull Licence licence) {
    validateLicence(licence);
    try {
      licence.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      licence.setUpdatedDate(licence.getCreatedDate());
      return licenceDao.save(licence);
    } catch (Exception e) {
      String message =
          String.format("Error while saving licence with  licenceType : %s",
              licence.getLicenceType());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update Licence</b>
   * @param id
   * @param licence
   * @return	returns Licence
   */
  @Override
  public Licence update(@NonNull String id, @NonNull Licence licence) {
    Licence existingLicence = licenceDao.findOne(id);

    if (ObjectUtils.isEmpty(existingLicence)) {
      String message = String.format("No licence found with id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    updateObject(licence, existingLicence);
    return licenceDao.save(existingLicence);
  }

  /**
   * <b>Update Licence with new Data </b>
   * @param licence
   * @param existingLicence
   */
  private void updateObject(@NonNull Licence licence, Licence existingLicence) {

    if (StringUtils.isEmpty(licence.getUpdatedBy())) {
      String message = String.format("Licence Updatedby not found");
      log.info(message);
      throw new IllegalArgumentException(message);
    }

    if (!StringUtils.isEmpty(licence.getLicenceType()))
      existingLicence.setLicenceType(licence.getLicenceType());

    existingLicence.setUpdatedBy(licence.getUpdatedBy());

    existingLicence.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
  }

  /**
   * <b>Validate Licence for Delete</b>
   * @param licence
   * @param id
   */
  private void validateLicenceDelete(Licence licence, @NonNull String id) {

    if (ObjectUtils.isEmpty(licence)) {
      String message = String.format("Error licence not found by licence id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    if (licence.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error licence already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * <b>Get Licence by id</b>
   * @param id
   * @return	returns Licence
   */
  @Override
  public Licence get(@NonNull String id) {
    try {
      return licenceDao.findOne(id);
    } catch (Exception e) {
      String message = String.format("Error while fetching licence by id %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Delete Licence By id and deltedBy</b>
   * @param id
   * @param updatedBy
   */
  @Override
  public void delete(@NonNull String id, @NonNull String updatedBy) {
    try {
      Licence licence = licenceDao.findOne(id);
      validateLicenceDelete(licence, updatedBy);
      licence.setStatus(StatusConstants.DELETED);
      licence.setUpdatedBy(updatedBy);
      licence.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      licence = licenceDao.save(licence);
      String message = String.format("Licence deleted by licence id %s", updatedBy);
      log.info(message);
    } catch (Exception e) {
      String message = String.format("Error while deleting licence by licence id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get All Licence</b>
   * @param pageable
   * @param filters
   * @return	returns Licence list
   */
  @Override
  public List<Licence> getAllLicences(Pageable pageable, List<Filter> filters) {
    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      return mongoTemplate.find(query, Licence.class);

    } catch (Exception e) {
      log.error("Error while fetching licences. ", e);
      throw new SocioSeerException("Error while fetching licences. ");
    }
  }

  /**
   * <b>Validate Licence</b>
   * @param licence
   */
  private void validateLicence( Licence licence) {

    if (StringUtils.isEmpty(licence.getLicenceType())) {
      log.info("Licence licenceType cannot be null/empty.");
      throw new IllegalArgumentException("Licence licenceType cannot be null/empty.");
    }

    if (StringUtils.isEmpty(licence.getCreatedBy())) {
      log.info("Licence createby cannot be null/empty.");
      throw new IllegalArgumentException("Licence createby cannot be null/empty.");
    }
  }

}
