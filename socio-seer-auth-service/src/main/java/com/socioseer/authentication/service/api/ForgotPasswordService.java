package com.socioseer.authentication.service.api;

import com.socioseer.common.domain.model.ForgotPassword;

/**
 * <b>ForgotPassword Service</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface ForgotPasswordService {
	/**
	 * <b>Save ForgotPassword</b>
	 * @param email
	 * @return returns ForgotPassword
	 */
	ForgotPassword save(String email);

	/**
	 * <b>Get ForgotPassword by Token</b>
	 * @param token
	 * @return returns ForgotPassword
	 */
	ForgotPassword getForgotPasswordByToken(String token);

	/**
	 * <b>Delete ForgotPassword</b>
	 * @param id
	 */
	void delete(String id);

	/**
	 * <b>Validate Token</b>
	 * @param token
	 * @return returns boolean
	 */
	boolean validateToken(String token);

}
