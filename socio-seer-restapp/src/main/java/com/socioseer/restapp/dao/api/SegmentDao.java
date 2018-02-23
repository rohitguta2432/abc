package com.socioseer.restapp.dao.api;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.Segment;

/**
 * <h3>Segment Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SegmentDao extends PagingAndSortingRepository<Segment, String> {

	/**
	 * 
	 * @param industryId
	 *            industryId as String
	 * @return List of Segments
	 */
	List<Segment> getSegmentsByIndustryId(String industryId);

}
