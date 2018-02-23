package com.socioseer.restapp.dao.api;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.AuthenticationToken;



/**
 * <h3>AuthToken Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface AuthTokenDao extends MongoRepository<AuthenticationToken, String> {

  /**
   * Gets the token  by user id.
   *
   * @param user id the user id
   * @return the authToken by user id
   */

  AuthenticationToken findOneByUserId(String userId);
  
  /**
   * delete the token  by user id.
   *
   * @param user id the user id
   * @return no return type
   */

  void deleteByUserId(String userId);

  /**
   * gets the authentication token  by auth token.
   *
   * @param authtoken the auth token
   * @return the authentication token
   */
  AuthenticationToken findOneByAuthToken(String authToken); 

}
