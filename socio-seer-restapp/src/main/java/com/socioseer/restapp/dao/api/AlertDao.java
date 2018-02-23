package com.socioseer.restapp.dao.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.Alert;

/**
 * <h3>Alert Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface AlertDao extends PagingAndSortingRepository<Alert, String> {

	/**
	 * 
	 * @param userId	
	 * @param status	integer data
	 * @return		returns integer data
	 */
	int countByUserIdAndStatus(String userId, int status);

	/**
	 * 
	 * @param userId
	 * @param pageable	
	 * @return		returns Alert as page
	 */
	Page<Alert> findByUserId(String userId, Pageable pageable);

	/**
	 * 
	 * @param userId
	 * @param status	integer data
	 * @return		returns Alert list
	 */
	List<Alert> findByUserIdAndStatus(String userId, int status);

}
