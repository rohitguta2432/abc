package com.socioseer.restapp.service.api;

import java.util.List;

import com.socioseer.common.domain.ResourceRoleMapping;

/**
 * <h3>ResourceRoleMapping Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface ResourceRoleMappingService extends CrudApi<ResourceRoleMapping> {

	/**
	 * <b>Get ResourceRoleMapping by url</b>
	 * @param url
	 *            url as String
	 * @return returns ResourceRoleMapping Object
	 */
	ResourceRoleMapping getResourceRoleMappingByUrl(String url);

	/**
	 * <b>Get All ResourceRoleMapping</b>
	 * @return returns List of ResourceRoleMapping Object
	 */
	List<ResourceRoleMapping> getAll();

}
