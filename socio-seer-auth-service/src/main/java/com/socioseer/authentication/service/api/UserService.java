package com.socioseer.authentication.service.api;

import com.socioseer.common.domain.User;

/**
 * <b>User Service</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface UserService {
	/**
	 * <b>Get User by email</b> 
	 * @param email
	 * @return returns User
	 */
	User getUserByEmail(String email);

	/**
	 * <b>Save User</b>
	 * @param user
	 * @return returns User
	 */
	User save(User user);

	/**
	 * <b>Get User by userId</b>
	 * @param userId
	 * @return returns User
	 */
	User get(String userId);

}
