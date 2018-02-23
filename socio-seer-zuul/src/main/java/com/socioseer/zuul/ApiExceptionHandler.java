package com.socioseer.zuul;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.socioseer.common.dto.Response;
import com.socioseer.zuul.exception.BadRequestException;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@ControllerAdvice
public class ApiExceptionHandler {

	/**
	 * 
	 * @param e
	 * @return returns String
	 */
	@ExceptionHandler(value = { BadRequestException.class, IllegalArgumentException.class })
	public ResponseEntity<Response<String>> handleAuthenticationFailedException(Exception e) {
		return new ResponseEntity<Response<String>>(new Response<String>(
				HttpStatus.BAD_REQUEST.value(), e.getMessage(), null), HttpStatus.BAD_REQUEST);
	}

}
