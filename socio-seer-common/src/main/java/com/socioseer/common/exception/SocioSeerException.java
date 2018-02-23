package com.socioseer.common.exception;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class SocioSeerException extends RuntimeException {

	private static final long serialVersionUID = 2771174581631905388L;

	public SocioSeerException() {
	}

	public SocioSeerException(String message) {
		super(message);
	}

	public SocioSeerException(Throwable cause) {
		super(cause);
	}

	public SocioSeerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SocioSeerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
