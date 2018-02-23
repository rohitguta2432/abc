package com.socioseer.restapp.dao.api;


import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.ResourceRoleMapping;

/**
 * <h3>ResourceRoleMapping Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface ResourceRoleMappingDao extends MongoRepository<ResourceRoleMapping, String> {

	/**
	 * 
	 * @param url url as String
	 * @return ResourceRoleMapping Object
	 */
	ResourceRoleMapping findOneByUrl(String url);

}
