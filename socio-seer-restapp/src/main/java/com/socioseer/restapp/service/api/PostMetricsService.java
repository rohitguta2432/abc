package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.PostMetrics;
import com.socioseer.common.dto.Filter;

/**
 * <h3>PostMetrics Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PostMetricsService extends CrudApi<PostMetrics> {

	/**
	   * <b>Save PostMetrics lists</b>
	   * @param postMetricsList
	   * @return	returns PostMetrics list
	   */
	List<PostMetrics> save(List<PostMetrics> postMetrics);

	  /**
	   * <b>Get All PostMetrics</b>
	   * @param pageable
	   * @param filters
	   * @return	returns PostMetrics list
	   */
	  List<PostMetrics> getAll(Pageable pageable, List<Filter> filters);

}
