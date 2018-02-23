package com.socioseer.restapp.service.impl;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.model.Country;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.CountryDao;
import com.socioseer.restapp.service.api.CountryService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>CountryService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class CountryServiceImpl implements CountryService {

  @Autowired
  private CountryDao countryDao;

  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * <b>Save Country</b>
   * @param		country
   * @return	returns Country
   */
  @Override
  public Country save(@NonNull Country country) {
    validateCountry(country);
    try {
      country.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      country.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return countryDao.save(country);
    } catch (Exception e) {
      String message =
          String.format("Error while saving country with  name : %s", country.getName());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate Country</b>
   * @param country
   */
  private void validateCountry( Country country) {

    if (StringUtils.isEmpty(country.getName())) {
      log.info("Country name cannot be null/empty.");
      throw new IllegalArgumentException("Country name cannot be null/empty.");
    }

    if (CollectionUtils.isEmpty(country.getStates())) {
      log.info("States cannot be null/empty.");
      throw new IllegalArgumentException("States cannot be null/empty.");
    }

    if (StringUtils.isEmpty(country.getCreatedBy())) {
      log.info("Create by user cannot be null/empty.");
      throw new IllegalArgumentException("Create by user cannot be null/empty.");
    }

  }

  /**
   * <b>Update Country</b>
   * @param id
   * @param country
   * @return	returns Country
   */
  @Override
  public Country update(@NonNull String id, @NonNull Country country) {
    Country existingCountry = countryDao.findOne(id);
    if (ObjectUtils.isEmpty(existingCountry)) {
      String message = String.format("No country found with id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    updateObject(country, existingCountry);
    return countryDao.save(existingCountry);
  }

  /**
   * <b>Update existing country with new data</b>
   * @param country
   * @param existingCountry
   */
  private void updateObject(Country country, Country existingCountry) {

    if (!StringUtils.isEmpty(country.getName()))
      existingCountry.setName(country.getName());

    if (!StringUtils.isEmpty(country.getStates()))
      existingCountry.setStates(country.getStates());

    if (StringUtils.isEmpty(country.getUpdatedBy())) {
      String message = String.format("Update by not found");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    existingCountry.setUpdatedBy(country.getUpdatedBy());
    existingCountry.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
  }

  /**
   * <b>Validate Country for Delete</b>
   * @param country
   * @param id
   */
  private void validateCountryDelete(Country country, String id) {

    if (country.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error country already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * <b>Get Country by id</b>
   * @param id
   * @return	returns Country
   */
  @Override
  public Country get(@NonNull String id) {
    return countryDao.findOne(id);
  }

  /**
   * <b>Delete Country By id</b>
   * @param id
   * @param updatedBy
   */
  @Override
  public void delete(@NonNull String id, @NonNull String updatedBy) {
    try {
      Country country = countryDao.findOne(id);
      validateCountryDelete(country, id);
      country.setStatus(StatusConstants.DELETED);
      country.setUpdatedBy(updatedBy);
      country.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      country = countryDao.save(country);
      String message = String.format("Country deleted by country id %s", updatedBy);
      log.info(message);
    } catch (Exception e) {
      String message = String.format("Error while fetching country by country id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get All Country list</b>
   * @param pageable
   * @param filters
   * @return			returns Country list
   */
  @Override
  public List<Country> getAllCountries(Pageable pageable, List<Filter> filters) {
    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      return mongoTemplate.find(query, Country.class);

    } catch (Exception e) {
      log.error("Error while fatching countries.", e);
      throw new SocioSeerException("Error while fetching countries.");
    }
  }

}
