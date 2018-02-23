package com.socioseer.restapp.dao.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.Team;

/**
 * <h3>Team Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface TeamDao extends MongoRepository<Team, String> {

	/**
	 * 
	 * @param clientId
	 *            clientId as String
	 * @return returns List of Teams
	 */
	List<Team> findAllByClientId(String clientId);

	/**
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param pageable
	 * @return returns List of Teams
	 */
	List<Team> findAllByClientId(String clientId, Pageable pageable);

	/**
	 * 
	 * @param name
	 *            name as String
	 * @return returns Team Object
	 */
	Optional<Team> findOneByName(String name);

}
