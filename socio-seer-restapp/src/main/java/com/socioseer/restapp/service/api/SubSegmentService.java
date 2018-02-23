package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.SubSegment;
import com.socioseer.common.dto.Filter;
import lombok.NonNull;

/**
 * <h3>SubSegment Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SubSegmentService extends CrudApi<SubSegment> {

	/**
	 * <b>Delete SubSegment by Id</b>
	 * 
	 * @param id
	 *            id as String
	 * @param updatedBy
	 *            updatedBy as String
	 */
	void delete(String id, String updatedBy);

	/**
	 * <b>Get All SubSegment</b>
	 * 
	 * @param pageable
	 * @param filters
	 */
	List<SubSegment> getAllSubSegments(Pageable pageable, List<Filter> filters);

	/**
	 * <b>Get All SubSegment By Segment Id</b>
	 * 
	 * @param segmentId  segmentId as String
	 */
	List<SubSegment> getAllSubSegmentsBySegmentId(@NonNull String segmentId);
	
}
