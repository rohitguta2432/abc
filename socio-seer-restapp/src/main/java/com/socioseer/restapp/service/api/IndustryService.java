package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.Industry;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Industry Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface IndustryService extends CrudApi<Industry> {
	/**
	 * <b>Delte Industry by id and deletdBy</b>
	 * @param id		industryId
	 * @param updatedBy
	 */
	void delete(String id, String updatedBy);
	/**
	 * <b>Get All Industries</b>
	 * @param pageable
	 * @param filters
	 * @return       returns Industry list
	 */
	List<Industry> getAllIndustries(Pageable pageable, List<Filter> filters);

}
