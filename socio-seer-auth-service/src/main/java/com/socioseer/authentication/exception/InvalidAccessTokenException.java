package com.socioseer.authentication.exception;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class InvalidAccessTokenException extends RuntimeException {

	private static final long serialVersionUID = -3207055787488461503L;

	/**
	 * 
	 */
	public InvalidAccessTokenException() {
	}

	/**
	 * '
	 * 
	 * @param message
	 */
	public InvalidAccessTokenException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public InvalidAccessTokenException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public InvalidAccessTokenException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InvalidAccessTokenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
