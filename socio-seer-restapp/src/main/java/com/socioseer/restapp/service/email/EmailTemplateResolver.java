package com.socioseer.restapp.service.email;

import com.socioseer.common.domain.EMAIL_TYPE;

/**
 * <h3>EmailTemplateResolver Services</h3>
 * @author OrangeMantra
 * @since  JDK 1.8
 * @version 1.0
 *
 */
public interface EmailTemplateResolver {

	/**
	 * 
	 * @param event		email type
	 * @param content	email body
	 * @return		returns String
	 */
	String resolve(EMAIL_TYPE event, Object content);
}
