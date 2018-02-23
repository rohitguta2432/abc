package com.socioseer.zuul.config;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.socioseer.common.dto.Response;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@FeignClient(value = "socio-seer-auth-service")
public interface AuthenticationService {
/**
 * 
 * @param authToken
 * @return Map<String, Object>
 */
	
	@RequestMapping(value = "/{authToken}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	Response<Map<String, Object>> validateAuthToken(@PathVariable("authToken") String authToken);

}
