package com.socioseer.restapp.service.impl;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.model.Industry;
import com.socioseer.common.domain.model.Segment;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.SegmentDao;
import com.socioseer.restapp.service.api.IndustryService;
import com.socioseer.restapp.service.api.SegmentService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>Segment Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class SegmentServiceImpl implements SegmentService {

  @Autowired
  private SegmentDao segmentDao;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private IndustryService industryService;

  /**
   * <b>Save Segment</b>
   * @param segment
   * @return returns Segment
   */
  @Override
  public Segment save(@NonNull Segment segment) {
    validateSegment(segment);
    try {
      segment.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      segment.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return segmentDao.save(segment);
    } catch (Exception e) {
      String message =
          String.format("Error while saving segment with  name : %s", segment.getSegmentName());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  /**
   * <b>Update Segment</b>
   * @param id
   * @param segment
   * @return returns Segment
   */
  @Override
  public Segment update(@NonNull String id, @NonNull Segment segment) {
    Segment existingSegment = get(id);
    if (ObjectUtils.isEmpty(existingSegment)) {
      String message = String.format("No segment found with id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    try {
      updateObject(segment, existingSegment);
      return segmentDao.save(existingSegment);
    } catch (Exception e) {
      String message = String.format("Error while updating segment with  id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  /**
   * <b>Get Segment by segmentId</b>
   * @param id
   * @return returns Segment
   */
  @Override
  public Segment get(String id) {
    try {
      return segmentDao.findOne(id);
    } catch (Exception e) {
      String message = String.format("Error while fetching segment with  id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
  /**
   * <b>Delete Segment</b>
   * @param id
   * @param updatedBy
   * 
   */
  @Override
  public void delete(String id, String updatedBy) {

    try {
      Segment segment = segmentDao.findOne(id);
      validateSegmentDelete(segment, updatedBy);
      segment.setStatus(StatusConstants.DELETED);
      segment.setUpdatedBy(updatedBy);
      segment.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      segment = segmentDao.save(segment);
      log.info(String.format("Segment with name : %s is deleted %s", segment.getSegmentName(),
          updatedBy));
    } catch (Exception e) {
      String message = String.format("Error while deleting segment by segment id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update Object</b>
   * @param segment
   * @param existingSegment
   */
  private void updateObject(@NonNull Segment segment, Segment existingSegment) {

    if (StringUtils.isEmpty(segment.getUpdatedBy())) {
      String message = String.format("Update by not found");
      log.info(message);
      throw new IllegalArgumentException(message);
    }

    if (!StringUtils.isEmpty(segment.getSegmentName()))
      existingSegment.setSegmentName(segment.getSegmentName());

    if (!StringUtils.isEmpty(segment.getIndustryId()))
      existingSegment.setIndustryId(segment.getIndustryId());
    if (!StringUtils.isEmpty(segment.getIndustryName()))
      existingSegment.setIndustryName(segment.getIndustryName());

    existingSegment.setUpdatedBy(segment.getUpdatedBy());

    existingSegment.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
  }

  /**
   * <b>Validate Segment for Delete</b>
   * @param segment
   * @param id
   */
  private void validateSegmentDelete(Segment segment, String id) {
    if (ObjectUtils.isEmpty(segment)) {
      String message = String.format("Error segment not found by segment id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    if (segment.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error segment already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }
  /**
   * <b>Get All Segments</b>
   * @param pageable
   * @param filters
   * @return returns List of Segment
   */
  @Override
  public List<Segment> getAllSegments(Pageable pageable, List<Filter> filters) {
    try {
      return mongoTemplate.find(QueryBuilder.createQuery(filters, pageable), Segment.class);
    } catch (Exception e) {
      log.error("Error while fetching segments.", e);
      throw new SocioSeerException("Error while fetching segments.");
    }
  }

  /**
   * <b>Get All Segment by industryId</b>
   * @param industryId
   * @return returns List Of Segment
   */
  @Override
  public List<Segment> getAllSegmentsByIndustryId(@NonNull String industryId) {
    try {
      return segmentDao.getSegmentsByIndustryId(industryId);
    } catch (Exception e) {
      String message = String.format("Error while fetching segments by industryId %s", industryId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate Segment</b>
   * @param segment
   */
  private void validateSegment(Segment segment) {

    if (StringUtils.isEmpty(segment.getSegmentName())) {
      log.info("SegmentName cannot be null/empty.");
      throw new IllegalArgumentException("SegmentName cannot be null/empty.");
    }
    if (StringUtils.isEmpty(segment.getIndustryId())) {
      log.info("Segment industryId cannot be null/empty.");
      throw new IllegalArgumentException("Segment industryId cannot be null/empty.");
    }
    if (StringUtils.isEmpty(segment.getIndustryName())) {
      log.info("Segment industryName cannot be null/empty.");
      throw new IllegalArgumentException("Segment industryName cannot be null/empty.");
    }
    if (StringUtils.isEmpty(segment.getCreatedBy())) {
      log.info("Segment createdby cannot be null/empty.");
      throw new IllegalArgumentException("Segment createdby cannot be null/empty.");
    }

    Industry industry = industryService.get(segment.getIndustryId());
    if (ObjectUtils.isEmpty(industry)) {
      String message = String.format("Industry not found by id %s", segment.getIndustryId());
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }
}
