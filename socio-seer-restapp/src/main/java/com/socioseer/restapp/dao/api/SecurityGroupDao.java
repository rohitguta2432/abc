package com.socioseer.restapp.dao.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.SecurityGroup;

/**
 * <h3>SecurityGroup Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SecurityGroupDao extends MongoRepository<SecurityGroup, String> {

	/**
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param pageable
	 * @return returns List of SecurityGroups
	 */
	Optional<List<SecurityGroup>> findAllByClientId(String clientId, Pageable pageable);

	/**
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param name
	 *            name as String
	 * @return SecurityGroup Object
	 */
	Optional<SecurityGroup> findOneByClientIdAndName(String clientId, String name);

	/**
	 * 
	 * @param name
	 *            name as String
	 * @return SecurityGroup Object
	 */
	SecurityGroup findOneByName(String name);
}
