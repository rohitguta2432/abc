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
import com.socioseer.common.domain.model.Industry;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.IndustryDao;
import com.socioseer.restapp.service.api.IndustryService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * <h3>IndustryService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class IndustryServiceImpl implements IndustryService {


  @Autowired
  private IndustryDao industryDao;

  @Autowired
  MongoTemplate mongoTemplate;

  /**
   * <b>Save Industry</b>
   * @param industry
   * @return	returns Industry
   */
  @Override
  public Industry save(@NonNull Industry industry) {
    
    validateIndustry(industry);
    
    try {
      industry.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      industry.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return industryDao.save(industry);
    } catch (Exception e) {
      String message = String.format("Error while saving industry with  name : %s",
          industry.getIndustryName());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate Industry</b>
   * @param industry
   */
  private void validateIndustry(Industry industry){
    if (StringUtils.isEmpty(industry.getIndustryName())) {
      log.info("IndustryName cannot be null/empty.");
      throw new IllegalArgumentException("IndustryName cannot be null/empty.");
    }
    if (StringUtils.isEmpty(industry.getCreatedBy())) {
      log.info("Industry CreatedBy cannot be null/empty.");
      throw new IllegalArgumentException("Industry CreatedBy cannot be null/empty.");
    }
  }
  
  /**
   * <b>Update Industry</b>
   * @param id
   * @param industry
   * @return	returns Industry
   */
  @Override
  public Industry update(@NonNull String id, @NonNull Industry industry) {
    Industry existingIndustry = industryDao.findOne(id);

    if (ObjectUtils.isEmpty(existingIndustry)) {
      String message = String.format("No industry found with id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    updateObject(industry, existingIndustry);
    return industryDao.save(existingIndustry);
  }

  /**
   * <b>Get Industry By Id</b>
   * @param id
   * @return	returns Industry
   */
  @Override
  public Industry get(String id) {
    try{
    return industryDao.findOne(id);
    }catch (Exception e) {
      String message = String.format("Error while fetching by id %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  
  /**
	 * <b>Delte Industry by id and deletdBy</b>
	 * @param id		industryId
	 * @param updatedBy
	 */
  @Override
  public void delete(String id, String updatedBy) {

    try {
      Industry industry = industryDao.findOne(id);
      validateIndustryDelete(industry, updatedBy);
      industry.setStatus(StatusConstants.DELETED);
      industry.setUpdatedBy(updatedBy);
      industry.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      industry = industryDao.save(industry);
      String message = String.format("Industry deleted by industry id %s", updatedBy);
      log.info(message);
    } catch (Exception e) {
      String message = String.format("Error while deleting industry by industry id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update Industry with new Data</b>
   * @param industry
   * @param existingIndustry
   */
  private void updateObject(Industry industry, Industry existingIndustry) {
    
    if (StringUtils.isEmpty(industry.getUpdatedBy())){
      String message = String.format("Update by not found");
      log.info(message);
      throw new IllegalArgumentException(message);
    }

    if (!StringUtils.isEmpty(industry.getIndustryName()))
      existingIndustry.setIndustryName(industry.getIndustryName());

    if (!StringUtils.isEmpty(industry.getUpdatedBy()))
      existingIndustry.setUpdatedBy(industry.getUpdatedBy());

    existingIndustry.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
  }

  /**
   * 
   * @param industry
   * @param id
   */
  private void validateIndustryDelete(Industry industry, String id) {
    if (ObjectUtils.isEmpty(industry)) {
      String message = String.format("Industry not find by industry id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    if (industry.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Industry already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
	 * <b>Get All Industries</b>
	 * @param pageable
	 * @param filters
	 * @return       returns Industry list
	 */
  @Override
  public List<Industry> getAllIndustries(Pageable pageable, List<Filter> filters) {
    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      return mongoTemplate.find(query, Industry.class);
    } catch (Exception e) {
      log.error("Error while fetching industries.", e);
      throw new SocioSeerException("Error while fetching industries.");
    }
  }

}
