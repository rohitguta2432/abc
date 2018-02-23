package com.socioseer.acl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.socioseer.common.dto.Response;
import com.socioseer.common.exception.UnAuthorizedException;


/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@ControllerAdvice
public class SocioSeerAclExceptionHandler {

	/**
	 * 
	 * @param e
	 * @return returns String
	 */ 
	@ExceptionHandler(value = { UnAuthorizedException.class })
	public ResponseEntity<Response<String>> handleUnauthorizedException(Exception e) {
		return new ResponseEntity<Response<String>>(new Response<String>(
				HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null), HttpStatus.UNAUTHORIZED);
	}

	/**
	 * 
	 * @param e
	 * @return returns String
	 */
	@ExceptionHandler(value = { IllegalArgumentException.class })
	public ResponseEntity<Response<String>> handleIllegalArgumentException(Exception e) {
		return new ResponseEntity<Response<String>>(new Response<String>(
				HttpStatus.BAD_REQUEST.value(), e.getMessage(), null), HttpStatus.BAD_REQUEST);
	}

}
