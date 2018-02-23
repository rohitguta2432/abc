package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.Country;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Country Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CountryService extends CrudApi<Country> {

  /**
   * <b>Delete Country By id</b>	
   * @param id          countryId
   * @param updatedBy
   */
  void delete(String id, String updatedBy);

  /**
   * <b>Get All Country list</b>
   * @param pageable
   * @param filters
   * @return			returns Country list
   */
  List<Country> getAllCountries(Pageable pageable, List<Filter> filters);
}
