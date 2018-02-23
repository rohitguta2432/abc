package com.socioseer.zuul.exception;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 3268668366772310869L;

	/**
	 * 
	 */
	public BadRequestException() {
	}

	/**
	 * 
	 * @param message
	 */
	public BadRequestException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public BadRequestException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}
/**
 * 
 * @param message
 * @param cause
 * @param enableSuppression
 * @param writableStackTrace
 */
	public BadRequestException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
