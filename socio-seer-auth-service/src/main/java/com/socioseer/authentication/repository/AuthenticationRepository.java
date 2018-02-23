package com.socioseer.authentication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.AuthenticationToken;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface AuthenticationRepository extends MongoRepository<AuthenticationToken, String> {
	/**
	 * 
	 * @param userId
	 * @return returns AuthenticationToken
	 */
	AuthenticationToken findOneByUserId(String userId);

	/**
	 * 
	 * @param userId
	 */
	void deleteByUserId(String userId);

	/**
	 * 
	 * @param authToken
	 * @return returns AuthenticationToken
	 */
	AuthenticationToken findOneByAuthToken(String authToken);
}
