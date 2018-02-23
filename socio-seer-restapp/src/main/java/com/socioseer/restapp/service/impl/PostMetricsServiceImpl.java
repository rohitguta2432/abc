package com.socioseer.restapp.service.impl;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import com.socioseer.common.domain.model.PostMetrics;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.PostMetricsDao;

import com.socioseer.restapp.service.api.PostMetricsService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>PostMetricsService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Service
@Slf4j
public class PostMetricsServiceImpl implements PostMetricsService {

  @Autowired
  private PostMetricsDao postMetricsDao;

  @Autowired
  private MongoTemplate mongoTemplate;


  /**
   * <b>Save PostMetrics</b>
   * @param postMetrics
   * @return	returns PostMetrics
   */
  @Override
  public PostMetrics save(PostMetrics postMetrics) {
    try {
      if (postMetrics == null) {
        throw new IllegalArgumentException("post metrics can not be null");
      }
      postMetrics.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return postMetricsDao.save(postMetrics);
    } catch (Exception e) {
      String message = "Error occurred while saving post metrics";
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update PostMetrics</b>
   * @param id
   * @param postMetrics
   * @return	returns PostMetrics
   */
  @Override
  public PostMetrics update(String id, PostMetrics postMetrics) {
    try {
      if (StringUtils.isEmpty(id)) {
        throw new IllegalArgumentException("id cannot be null or empty");
      }
      PostMetrics existing = postMetricsDao.findOne(id);
      if (existing == null) {
        throw new SocioSeerException("post metrics can not be found for id " + id);
      }
      validatePostMetrics(postMetrics);
      postMetrics.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      postMetrics.setId(existing.getId());
      return postMetricsDao.save(postMetrics);
    } catch (Exception e) {
      String message = String.format("Error while fetching post metrics for id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get PostMetrics by id</b>
   * @param id
   * @return	returns PostMetrics
   */
  @Override
  public PostMetrics get(String id) {
    try {
      if (StringUtils.isEmpty(id)) {
        throw new IllegalArgumentException("Id cannot be null or empty");
      }
      return postMetricsDao.findOne(id);
    } catch (Exception e) {
      String message = String.format("Error while fetching post metrics for id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Save PostMetrics lists</b>
   * @param postMetricsList
   * @return	returns PostMetrics list
   */
  @Override
  public List<PostMetrics> save(List<PostMetrics> postMetricsList) {
    try {
      if (CollectionUtils.isEmpty(postMetricsList)) {
        log.info("Post metrics list cannot be null or empty");
        throw new IllegalArgumentException("Post metrics list cannot be null or empty");
      }
      postMetricsList.forEach(postMetrics -> {
        validatePostMetrics(postMetrics);
        postMetrics.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      });
      return (List<PostMetrics>) postMetricsDao.save(postMetricsList);
    } catch (Exception e) {
      String message = "Error while saving post metrics";
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validation PostMetrics</b>
   * @param postMetrics
   */
  private void validatePostMetrics(PostMetrics postMetrics) {
  
	  if (StringUtils.isEmpty(postMetrics.getCreatedBy())) {
      log.info("Created by user id can not be null/empty.");
      throw new IllegalArgumentException("Created by user id can not be null/empty.");
    }

    if (StringUtils.isEmpty(postMetrics.getName())) {
      log.info("Name can not be null/empty.");
      throw new IllegalArgumentException("Name can not be null/empty.");
    }

    if (StringUtils.isEmpty(postMetrics.getUnit())) {
      log.info("Unit cannot be null/empty.");
      throw new IllegalArgumentException("Unit cannot be null/empty.");
    }
  }

  /**
   * <b>Get All PostMetrics</b>
   * @param pageable
   * @param filters
   * @return	returns PostMetrics list
   */
  @Override
  public List<PostMetrics> getAll(Pageable pageable, List<Filter> filters) {
	
	    try {
	      Query query = QueryBuilder.createQuery(filters, pageable);
	     return  mongoTemplate.find(query, PostMetrics.class);
	    } catch (Exception e) {
	      log.error("Error while fetching postMetrics.", e);
	      throw new SocioSeerException("Error while fetching postMetrics.");
	    }
	  }

}