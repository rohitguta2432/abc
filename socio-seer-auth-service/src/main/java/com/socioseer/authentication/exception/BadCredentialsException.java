package com.socioseer.authentication.exception;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */

public class BadCredentialsException extends RuntimeException {

	private static final long serialVersionUID = 1932603799876896358L;

	/**
	 * 
	 */
	public BadCredentialsException() {
	}

	/**
	 * 
	 * @param message
	 */
	public BadCredentialsException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public BadCredentialsException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public BadCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public BadCredentialsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
