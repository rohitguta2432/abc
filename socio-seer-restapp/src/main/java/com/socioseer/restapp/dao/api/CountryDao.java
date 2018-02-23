package com.socioseer.restapp.dao.api;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.Country;

/**
 * <h3>Country Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CountryDao extends PagingAndSortingRepository<Country, String> {

}
