package com.socioseer.restapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.socioseer.common.dto.Response;
import com.socioseer.common.exception.SocioSeerException;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@ControllerAdvice
public class SocioSeerRestAppExceptionHandler {

  /**
   * Handle socio seer exception.
   *
   * @param e the e
   * @return the response entity
   */
  @ExceptionHandler(SocioSeerException.class)
  public ResponseEntity<Response<String>> handleSocioSeerException(Exception e) {
    return new ResponseEntity<Response<String>>(new Response<String>(
        HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handle illegal argument exception.
   *
   * @param e the exception
   * @return the response entity
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Response<String>> handleIllegalArgumentException(Exception e) {
    return new ResponseEntity<Response<String>>(new Response<String>(
        HttpStatus.BAD_REQUEST.value(), e.getMessage(), null), HttpStatus.BAD_REQUEST);
  }
  
}
