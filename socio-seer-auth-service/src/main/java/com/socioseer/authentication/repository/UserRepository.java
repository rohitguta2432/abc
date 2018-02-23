package com.socioseer.authentication.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.User;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface UserRepository extends MongoRepository<User, String> {
/**
 * 
 * @param email
 * @return returns User
 */
	User findOneByEmail(String email);
}
