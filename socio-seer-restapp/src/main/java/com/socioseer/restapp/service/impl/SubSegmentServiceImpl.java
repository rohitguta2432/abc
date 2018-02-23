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
import com.socioseer.common.domain.model.Segment;
import com.socioseer.common.domain.model.SubSegment;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.SubSegmentDao;
import com.socioseer.restapp.service.api.SegmentService;
import com.socioseer.restapp.service.api.SubSegmentService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>SubSegment Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class SubSegmentServiceImpl implements SubSegmentService {

	@Autowired
	private SubSegmentDao subSegmentDao;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SegmentService segmentService;

	/**
	 * <b>Save SubSegment</b>
	 * 
	 * @param subSegment
	 * @return returns SubSegment
	 */
	@Override
	public SubSegment save(@NonNull SubSegment subSegment) {
		validateSubSegment(subSegment);
		try {
			subSegment.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			subSegment.setUpdatedDate(subSegment.getCreatedDate());
			return subSegmentDao.save(subSegment);
		} catch (Exception e) {
			String message = String.format("Error while saving subsegment with  name : %s",
					subSegment.getSubSegmentName());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update SubSegment</b>
	 * 
	 * @param id
	 * @param subSegment
	 * @return returns SubSegment
	 */
	@Override
	public SubSegment update(@NonNull String id, @NonNull SubSegment subSegment) {
		SubSegment existingSubSegment = get(id);
		if (ObjectUtils.isEmpty(existingSubSegment)) {
			String message = String.format("No subsegment found with id : %s", id);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		updateObject(subSegment, existingSubSegment);
		return subSegmentDao.save(existingSubSegment);
	}

	/**
	 * <b>Get SubSegment by Id</b>
	 * 
	 * @param id
	 * @return returns SubSegment
	 */
	@Override
	public SubSegment get(String id) {
		try {
			return subSegmentDao.findOne(id);
		} catch (Exception e) {
			String message = String.format("Error while fetching subsement by id %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Delete SubSegment</b>
	 * 
	 * @param id
	 * @param updatedBy
	 * @return returns SubSegment
	 */
	@Override
	public void delete(String id, String updatedBy) {

		try {
			SubSegment subSegment = subSegmentDao.findOne(id);
			validateSubSegmentDelete(subSegment, updatedBy);
			subSegment.setStatus(StatusConstants.DELETED);
			subSegment.setUpdatedBy(updatedBy);
			subSegment.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			subSegment = subSegmentDao.save(subSegment);
			log.info(String.format("SubSegment with name : %s is deleted by %s", subSegment.getSubSegmentName(),
					updatedBy));
		} catch (Exception e) {
			String message = String.format("Error while deleting subSegment by id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get All SubSegments</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @return returns List of SubSegment
	 */
	@Override
	public List<SubSegment> getAllSubSegments(Pageable pageable, List<Filter> filters) {
		try {
			return mongoTemplate.find(QueryBuilder.createQuery(filters, pageable), SubSegment.class);
		} catch (Exception e) {
			log.error("Error while fetching subsegments.", e);
			throw new SocioSeerException("Error while fetching subsegments.");
		}
	}

	/**
	 * <b>Get All SubSegment by segmentId</b>
	 * 
	 * @param segmentId
	 * @return returns List of SubSegment
	 */
	@Override
	public List<SubSegment> getAllSubSegmentsBySegmentId(@NonNull String segmentId) {

		try {
			return subSegmentDao.getSubSegmentsBySegmentId(segmentId);
		} catch (Exception e) {
			String message = String.format("Error while fetching subsegments by segmentId %s", segmentId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update Object</b>
	 * 
	 * @param subSegment
	 * @param existingSubSegment
	 */
	private void updateObject(@NonNull SubSegment subSegment, SubSegment existingSubSegment) {

		if (StringUtils.isEmpty(subSegment.getUpdatedBy())) {
			String message = String.format("Update by not found");
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		if (!StringUtils.isEmpty(subSegment.getSubSegmentName())) {
			existingSubSegment.setSubSegmentName(subSegment.getSubSegmentName());
		}

		if (!StringUtils.isEmpty(subSegment.getSegmentId())) {
			existingSubSegment.setSegmentId(subSegment.getSegmentId());
		}
		existingSubSegment.setUpdatedBy(subSegment.getUpdatedBy());
		existingSubSegment.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
	}

	/**
	 * <b>Validate SubSegment for Delete</b>
	 * 
	 * @param subSegment
	 * @param id
	 */
	private void validateSubSegmentDelete(SubSegment subSegment, String id) {
		if (ObjectUtils.isEmpty(subSegment)) {
			String message = String.format("Error subsegment not find by subsegment id : %s", id);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		if (subSegment.getStatus() == StatusConstants.DELETED) {
			String message = String.format("Error subsegment already deleted");
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Validate SubSegment</b>
	 * 
	 * @param subSegment
	 */
	private void validateSubSegment(@NonNull SubSegment subSegment) {

		if (StringUtils.isEmpty(subSegment.getSubSegmentName())) {
			log.info("SubSegmentName cannot be null/empty.");
			throw new IllegalArgumentException("SubSegmentName cannot be null/empty.");
		}
		if (StringUtils.isEmpty(subSegment.getSegmentId())) {
			log.info("SubSegment industryname cannot be null/empty.");
			throw new IllegalArgumentException("SubSegment industryname cannot be null/empty.");
		}

		if (StringUtils.isEmpty(subSegment.getCreatedBy())) {
			log.info("SubSegment createdby cannot be null/empty.");
			throw new IllegalArgumentException("SubSegment createdby cannot be null/empty.");

		}

		Segment segment = segmentService.get(subSegment.getSegmentId());
		if (ObjectUtils.isEmpty(segment)) {
			String message = String.format("Segment not found by id %s", subSegment.getSegmentId());
			log.info(message);
			throw new IllegalArgumentException(message);
		}

	}

}
