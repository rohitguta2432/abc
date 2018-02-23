package com.socioseer.authentication.exception;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class UserAccountInactiveExcpetion extends RuntimeException {

	private static final long serialVersionUID = -3140390393308363387L;

	/**
	 * 
	 */
	public UserAccountInactiveExcpetion() {
	}

	/**
	 * 
	 * @param message
	 */
	public UserAccountInactiveExcpetion(String message) {
		super(message);
	}

	/**
	 * 
	 * @param cause
	 */
	public UserAccountInactiveExcpetion(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public UserAccountInactiveExcpetion(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public UserAccountInactiveExcpetion(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
