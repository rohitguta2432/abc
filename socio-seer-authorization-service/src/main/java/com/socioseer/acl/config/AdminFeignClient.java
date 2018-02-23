package com.socioseer.acl.config;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.socioseer.common.domain.ResourceRoleMapping;
import com.socioseer.common.domain.User;
import com.socioseer.common.dto.Response;

/**
 * <b>AdminFeignClient Configuration</b>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@FeignClient(value = "socio-seer-restapp")
public interface AdminFeignClient {

	/**
	 * <b>Fetch All Resource Mapping</b>
	 * @param secretKey
	 * @return returns ResourceRoleMapping List
	 */
	@RequestMapping(value = "role-url-mapping/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response<List<ResourceRoleMapping>>> fetchAllResourceRoleMapping(
			@RequestHeader("X-AUTH-HEADER") String secretKey);
/**
 * <b>Fetch User By Id</b>
 * @param userId
 * @param secretKey
 * @return returns User
 */
	@RequestMapping(value = "user/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response<User>> fetchUserById(@PathVariable("userId") String userId,
			@RequestHeader("X-AUTH-HEADER") String secretKey);

}
