package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.AudienceType;
import com.socioseer.common.dto.Filter;

/**
 * <h3>AudienceType Services.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface AudienceTypeService extends CrudApi<AudienceType> {

	/**
	   * <b>Delete AudienceType by id</b>
	   * @param		id
	   * @param		updatedBy
	   */
  void delete(String id, String updatedBy);

  /**
   * <b>Get all AudienceTypes</b>
   * @param		pageable
   * @param		filters
   * @return	returns AudienceType list
   */
  List<AudienceType> getAllAudienceTypes(Pageable pageable, List<Filter> filters);
 
 
}
