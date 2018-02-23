package com.socioseer.authentication;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.socioseer.authentication.exception.BadCredentialsException;
import com.socioseer.authentication.exception.InvalidAccessTokenException;
import com.socioseer.authentication.exception.UserAccountInactiveExcpetion;
import com.socioseer.common.dto.Response;
import com.socioseer.common.exception.ExpiredAuthenticationTokenException;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@ControllerAdvice
public class SocioSeerAuthExceptionHandler {

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = { InvalidAccessTokenException.class, BadCredentialsException.class,
			ExpiredAuthenticationTokenException.class, UserAccountInactiveExcpetion.class })
	public ResponseEntity<Response<String>> handleAuthenticationFailedException(Exception e) {
		return new ResponseEntity<Response<String>>(new Response<String>(
				HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null), HttpStatus.UNAUTHORIZED);
	}
}
