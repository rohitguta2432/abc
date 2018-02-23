package com.socioseer.restapp.service.email;

import java.util.List;

import com.socioseer.common.domain.EMAIL_TYPE;

/**
 * <h3>Email Services</h3>
 * @author OrangeMantra
 * @since  JDK 1.8
 * @version 1.0
 *
 */
public interface EmailService {

	/**
	 * 
	 * @param type			email type
	 * @param context		email body
	 * @param recipentEmail	
	 */
	public void sendEmail(EMAIL_TYPE type, Object context, String recipentEmail);

	/**
	 * 
	 * @param type			email type
	 * @param context		email body	
	 * @param recipentEmail	recipentEmail list
	 */
	public void sendEmail(EMAIL_TYPE type, Object context, List<String> recipentEmail);

}
