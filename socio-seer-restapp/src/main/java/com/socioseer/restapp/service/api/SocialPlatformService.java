package com.socioseer.restapp.service.api;


import java.util.List;

import com.socioseer.common.domain.model.campaign.SocialPlatform;

/**
 * <h3>SocialPlatform Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SocialPlatformService extends CrudApi<SocialPlatform> {

	/**
	 * <b>Get SocialPlatform by Name</b>
	 * 
	 * @param name
	 *            name as String
	 * @return returns SocialPlatform.
	 */
  SocialPlatform getPlatformByName(String name);

  /**
	 * <b>Get All SocialPlatforms</b>
	 * 
	 * @return returns List of SocialPlatforms.
	 */
  List<SocialPlatform> getAllPlatforms();
  
}
