package com.socioseer.authentication.repository;

import org.springframework.data.repository.CrudRepository;

import com.socioseer.common.domain.model.ForgotPassword;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface ForgotPasswordDao extends CrudRepository<ForgotPassword, String> {
/**
 * 
 * @param userId
 * @return returns ForgotPassword
 */
  ForgotPassword findOneByUserId(String userId);
/**
 * 
 * @param token
 * @return returns ForgotPassword
 */
  ForgotPassword findOneByToken(String token);

}
