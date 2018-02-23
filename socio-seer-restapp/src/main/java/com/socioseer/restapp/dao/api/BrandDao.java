package com.socioseer.restapp.dao.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.model.Brand;

/**
 * <h3>Brand Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface BrandDao extends MongoRepository<Brand, String> {
  
	/**
	 * 
	 * @param cliendId
	 * @param status	integer data
	 * @return	returns Brand list
	 */
	Optional<List<Brand>> findAllByClientIdAndStatus(String cliendId, int status);

	/**
	 * 
	 * @param cliendId
	 * @param name
	 * @return		returns Brand 
	 */
	Optional<Brand> findOneByClientIdAndName(String cliendId, String name);

	/**
	 * 
	 * @param clientId
	 * @return		returns integer data
	 */
	int countByClientId(String clientId);

	/**
	 * 
	 * @param clientId
	 * @return		returns Brand list
	 */
	List<Brand> getBrandsByClientId(String clientId);
}
