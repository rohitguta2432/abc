package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.Licence;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Licence Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface LicenceService extends CrudApi<Licence> {
	
	/**
	   * <b>Delete Licence By id and deltedBy</b>
	   * @param id
	   * @param updatedBy
	   */
	void delete(String id, String updatedBy);

	/**
	   * <b>Get All Licence</b>
	   * @param pageable
	   * @param filters
	   * @return	returns Licence list
	   */
	List<Licence> getAllLicences(Pageable pageable, List<Filter> filters);
}
