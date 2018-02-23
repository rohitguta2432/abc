package com.socioseer.acl.service.api;

import java.util.Map;

/**
 * <b>Authorization Service</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface AuthorizationService {

	/**
	 * <b>Resource Validation</b>
	 * @param queryMap
	 * @return returns boolean
	 */
	boolean hasAccessToRequestedResource(Map<String, String> queryMap);

	/**
	 * 
	 */
	void reloadAuthorizationMap();

}
