package com.socioseer.restapp.dao.api;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.Client;

/**
 * <h3>Client Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface ClientDao extends PagingAndSortingRepository<Client, String> {
	
	/**
	 * 
	 * @param clientName
	 * @return		returns	client
	 */
	Client getByClientName(String clientName);

}
