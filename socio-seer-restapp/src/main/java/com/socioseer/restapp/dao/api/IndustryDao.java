package com.socioseer.restapp.dao.api;

import org.springframework.data.repository.PagingAndSortingRepository;
import com.socioseer.common.domain.model.Industry;

/**
 * <h3>Industry Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface IndustryDao extends PagingAndSortingRepository<Industry, String> {

}
