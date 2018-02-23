package com.socioseer.authentication.service.impl;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.socioseer.authentication.repository.ForgotPasswordDao;
import com.socioseer.authentication.service.api.ForgotPasswordService;
import com.socioseer.authentication.service.api.UserService;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.ForgotPassword;
import com.socioseer.common.exception.SocioSeerException;

/**
 * <b>Forgot Password Service Implementation</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

  @Autowired
  private ForgotPasswordDao forgotPasswordDao;

  @Autowired
  private UserService userService;

  @Value("${forgot.password.expiration}")
  private int expirationTimeInMinute;

  /**
   * <b>Save ForgotPassword</b>
   * @param email
   * @return returns ForgotPassword
   */
  @Override
  public ForgotPassword save(@NonNull String email) {

    User existingUser = userService.getUserByEmail(email);

    if (existingUser == null) {
      String message = String.format("No user found with email : %s", email);
      log.info(message);
      throw new IllegalArgumentException(message);
    }

    ForgotPassword existingForgotPassword = forgotPasswordDao.findOneByToken(email);
    if (existingForgotPassword != null) {
      if (isValid(existingForgotPassword)) {
        existingForgotPassword.setGeneratedAt(System.currentTimeMillis());
        return forgotPasswordDao.save(existingForgotPassword);
      } else {
        log.info(String.format(
            "Forgot password token with id : %s for user : %s has expired, hence deleting it",
            existingForgotPassword.getId(), existingForgotPassword.getUserId()));
        forgotPasswordDao.delete(existingForgotPassword.getId());
      }
    }

    ForgotPassword forgotPassword = new ForgotPassword();
    forgotPassword.setUserId(existingUser.getId());
    forgotPassword.setToken(UUID.randomUUID().toString());
    forgotPassword.setGeneratedAt(System.currentTimeMillis());

    try {
      return forgotPasswordDao.save(forgotPassword);
    } catch (Exception e) {
      String message =
          String.format("Error while saving forgot password token for user : %s",
              existingUser.getEmail());
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

  /**
   * <b>Get ForgotPassword by Token</b>
   * @param token
   */
  @Override
  public ForgotPassword getForgotPasswordByToken(@NonNull String token) {

    try {
      return forgotPasswordDao.findOneByToken(token);
    } catch (Exception e) {
      String message = String.format("Error while fetching forgot password by token : %s", token);
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

  /**
   * <b>Delete ForgotPassword<>
   * @param id
   */
  @Override
  public void delete(@NonNull String id) {

    try {
      forgotPasswordDao.delete(id);
    } catch (Exception e) {
      String message = String.format("Error while deleting forgot password token by id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

  /**
   * <b>Validate Token</b>
   * @param token
   */
  @Override
  public boolean validateToken(@NonNull String token) {

    ForgotPassword forgotPassword = forgotPasswordDao.findOneByToken(token);
    if (forgotPassword == null) {
      log.info("Invalid token.");
      throw new IllegalArgumentException("Invalid token.");
    }
    return isValid(forgotPassword);
  }


  /***
   * <b>Check Validity of ForgotPassword</b>
   * @param forgotPassword
   * @return returns boolean
   */
  private boolean isValid(ForgotPassword forgotPassword) {
    long generatedAt = forgotPassword.getGeneratedAt();
    return ((System.currentTimeMillis() - generatedAt) < TimeUnit.MINUTES
        .toMillis(expirationTimeInMinute));
  }


}
