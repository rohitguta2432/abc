package com.socioseer.restapp.dao.api;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.model.campaign.SocialPlatform;

/**
 * <h3>SocialPlatform Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SocialPlatformDao extends MongoRepository<SocialPlatform, String> {

	/**
	 * 
	 * @param name
	 *            name as String
	 * @return returns SocialPlatform Object
	 */
	SocialPlatform findOneByName(String name);

}
