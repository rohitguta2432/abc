package com.socioseer.restapp.service.api;


import com.socioseer.common.domain.model.Audience;

/**
 * <h3>Audience Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface AudienceService extends CrudApi<Audience>{
	/**
	 * <b>Check Audience by audienceId </b>
	 * @param audienceId
	 * @return returns boolean
	 */
  boolean isExists(String audienceId);
  
  
}
