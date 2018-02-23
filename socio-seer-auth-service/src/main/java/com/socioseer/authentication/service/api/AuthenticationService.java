package com.socioseer.authentication.service.api;

import java.util.Map;

import com.socioseer.common.domain.AuthenticationToken;

/**
 * <b>Authentication Service</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface AuthenticationService {

	/**
	 * <b>Generate Token</b>
	 * @param email
	 * @param password
	 * @param client
	 * @return returns AuthenticationToken
	 */
	AuthenticationToken generateToken(String email, String password, String client);

	/**
	 * <b>Get AuthenticationToken by UserId</b>
	 * @param userId
	 * @return returns AuthenticationToken
	 */
	AuthenticationToken getAuthenticationTokenByUserId(String userId);

	/**
	 * <b>Delete Authentication by UserId</b>
	 * @param userId
	 */
	void deleteAuthenticationTokenByUserId(String userId);

	/**
	 * <b>Validate Authentication</b>
	 * @param authenticationToken
	 * @return returns Map<String, Object>
	 */
	Map<String, Object> validateToken(String authenticationToken);

	/**
	 * <b>Forgot Password</b>
	 * @param email
	 */
	void forgotPassword(String email);

	/**
	 * <b>Reset Password</b>
	 * @param map
	 */
	void restPassword(Map<String, String> map);

}
