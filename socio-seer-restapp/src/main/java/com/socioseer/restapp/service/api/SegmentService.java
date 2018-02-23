package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.Segment;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Segment Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SegmentService extends CrudApi<Segment> {

	/**
	 * <b>Delete Segment by Id</b>
	 * 
	 * @param id
	 *            id as String
	 * @param updatedBy
	 *            updatedBy as String
	 */
	void delete(String id, String updatedBy);

	/**
	 * <b>Get All Segment</b>
	 * 
	 * @param pageable
	 * @param filters
	 */
	List<Segment> getAllSegments(Pageable pageable, List<Filter> filters);

	/**
	 * <b>Get All Segment By industryId</b>
	 * 
	 * @param industryId
	 *            industryId as String
	 */
	List<Segment> getAllSegmentsByIndustryId(String industryId);

}
