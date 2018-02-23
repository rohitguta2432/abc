package com.socioseer.restapp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.model.Currency;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.CurrencyDao;
import com.socioseer.restapp.service.api.CurrencyService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * <h3>CurrencyService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class CurrencyServiceImpl implements CurrencyService {

  @Autowired
  private CurrencyDao currencyDao;
  
  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * <b>Save Currency</b>
   * @param currency
   * @return	returns Currency
   */
  @Override
  public Currency save(@NonNull Currency currency) {
    currencyValidation(currency);
    try {
      currency.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      currency.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return currencyDao.save(currency);
    } catch (Exception e) {
      String message =
          String.format("Error while saving currency with name : %s", currency.getName());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate currency</b>
   * @param currency
   */
  private void currencyValidation(Currency currency) {

    if (StringUtils.isEmpty(currency.getName())) {
      log.info("Currency name cannot be null/empty.");
      throw new IllegalArgumentException("Currency name cannot be null/empty.");
    }

    if (StringUtils.isEmpty(currency.getCreatedBy())) {
      log.info("Currency createdby cannot be null/empty.");
      throw new IllegalArgumentException("Currency createdby cannot be null/empty.");
    }
  }

  /**
   * <b>Update Currency</b>
   * @param id
   * @param currency
   * @return returns Currency
   */
  @Override
  public Currency update(@NonNull String id, @NonNull Currency currency) {
    try {
      updateValidation(id, currency);
      return currencyDao.save(currency);
    } catch (Exception e) {
      String message = String.format("Error while updating currency with  id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>validate currency for update</b>
   * @param id
   * @param currency
   */
  private void updateValidation(@NonNull String id, @NonNull Currency currency) {

    Currency existedCurrency = currencyDao.findOne(id);
    if (ObjectUtils.isEmpty(existedCurrency)) {
      String message = String.format("Currency not found by id %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }

    if (StringUtils.isEmpty(currency.getName())) {
      log.info("Currency name cannot be null/empty.");
      throw new IllegalArgumentException("Currency name cannot be null/empty.");
    }

    if (StringUtils.isEmpty(currency.getUpdatedBy())) {
      log.info("Currency updated by not found");
      throw new IllegalArgumentException("Currency updated by not found");
    }

    currency.setId(id);
    currency.setCreatedBy(existedCurrency.getCreatedBy());
    currency.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
  }

  /**
   * <b>Get Currency by id</b>
   * @param id
   * @return	returns Currency
   */
  @Override
  public Currency get(@NonNull String id) {
    try {
      return currencyDao.findOne(id);
    } catch (Exception e) {
      String message = String.format("Error while fetching currency with  id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Delete Currency by currencyId</b>	
   * @param id			currencyId
   * @param updatedBy
   */
  @Override
  public void delete(String id, String updatedBy) {
    try {
      Currency currency = currencyDao.findOne(id);
      validateCurrencyDelete(currency, updatedBy);
      currency.setStatus(StatusConstants.DELETED);
      currency.setUpdatedBy(updatedBy);
      currency.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      currency = currencyDao.save(currency);
      String message = String.format("Currency deleted by currency id %s", updatedBy);
      log.info(message);
    } catch (Exception e) {
      String message = String.format("Error while deleting currency by currency id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }

  }
  
  /**
   * <b>Validate currency for Delete</b>
   * @param currency
   * @param id
   */
  private void validateCurrencyDelete(Currency currency, @NonNull String id) {

    if (ObjectUtils.isEmpty(currency)) {
      String message = String.format("Error currency not found by currency id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    if (currency.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error currency already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * <b>Get All Currency list</b>
   * @param pageable
   * @param filters
   * @return			returns Currency list
   */
  @Override
  public List<Currency> getAllCurrencies(Pageable pageable, List<Filter> filters) {
    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      return mongoTemplate.find(query, Currency.class);

    } catch (Exception e) {
      log.error("Error while fetching currency.", e);
      throw new SocioSeerException("Error while fetching currency");
    }
    
  }
  
  /**
   * <b>Get Currency Count</b>
   * @return	integer data
   */
  @Override
  public int count(){
    try{
      return (int) currencyDao.count();
    }catch(Exception e){
      log.error("Error while fetching currency.", e);
      throw new SocioSeerException("Error while fetching currency");
    }
    
  }


}
