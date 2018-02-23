package com.socioseer.acl.service.impl;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socioseer.acl.config.AdminFeignClient;
import com.socioseer.acl.service.ResourceRoleMappingCache;
import com.socioseer.acl.service.api.AuthorizationService;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.domain.User;
import com.socioseer.common.dto.Response;
import com.socioseer.common.exception.SocioSeerException;

/**
 * <b>Authorization Service Implementation</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	@Value("${socio.seer.secret.key}")
	private String secretKey;

	@Autowired
	private AdminFeignClient adminFeignClient;

	@Autowired
	private ResourceRoleMappingCache resourceRoleMappingCache;

	/**
	 * Method to validate whether user has access to requested resource.
	 * 
	 * @param - query parameter map
	 */
	@Override
	public boolean hasAccessToRequestedResource(final Map<String, String> queryMap) {

		validateQueryParameterMap(queryMap);

		final String userId = queryMap.get(SocioSeerConstant.KEY_USER_ID);
		final String requestedResource = queryMap.get(SocioSeerConstant.KEY_REQUESTED_RESOURCES);
		final String httpMethod = queryMap.get(SocioSeerConstant.KEY_HTTP_METHOD);

		final User existingUser = getUserById(userId);

		if (existingUser == null) {
			final String message = String.format("No user found with id : %s", userId);
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		return resourceRoleMappingCache.hasAccess(existingUser.getSecurityGroups(),
				requestedResource, httpMethod);
	}

	/**
	 * Method to reload cache.
	 */
	@Override
	public void reloadAuthorizationMap() {
		resourceRoleMappingCache.reloadCache();
	}

	/**
	 * Method to fetch user by given id.
	 * 
	 * @param userId
	 *            - unique identifier for user.
	 * 
	 * @return - {@link User}
	 */
	private User getUserById(final String userId) {

		final ResponseEntity<Response<User>> response = adminFeignClient.fetchUserById(userId,
				secretKey);
		if (response.getStatusCode() != HttpStatus.OK) {
			final String message = String.format(
					"API responded with error code %d while fetcing user by id : %s",
					response.getStatusCodeValue(), userId);
			log.error(message);
			throw new SocioSeerException(message);
		}
		return response.getBody().getData();
	}

	/**
	 * Method to validate incoming query parameter map.
	 * 
	 * @param queryMap
	 *            - query parameter map to be validated.
	 */
	private void validateQueryParameterMap(final Map<String, String> queryMap) {

		if (CollectionUtils.isEmpty(queryMap)) {
			log.error("Query parameter map cannot be empty/null.");
			throw new IllegalArgumentException("Query parameter map cannot be empty/null.");
		}

		if (!queryMap.containsKey(SocioSeerConstant.KEY_USER_ID)) {
			log.error("User id not found in query parameter map.");
			throw new IllegalArgumentException("User id not found in query parameter map.");

		}

		if (!queryMap.containsKey(SocioSeerConstant.KEY_REQUESTED_RESOURCES)) {
			log.error("Requested resource not found in query parameter map.");
			throw new IllegalArgumentException(
					"Requested resource not found in query parameter map.");

		}

		if (!queryMap.containsKey(SocioSeerConstant.KEY_HTTP_METHOD)) {
			log.error("Requested method not found in query parameter map.");
			throw new IllegalArgumentException("Requested method not found in query parameter map.");

		}
	}

}
