package com.socioseer.restapp.dao.api;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.SubSegment;

/**
 * <h3>SubSegment Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SubSegmentDao extends PagingAndSortingRepository<SubSegment, String> {

	/**
	 * 
	 * @param segmentId
	 *            segmentId as String
	 * @return returns List of SubSegment
	 */
	List<SubSegment> getSubSegmentsBySegmentId(String segmentId);

}
