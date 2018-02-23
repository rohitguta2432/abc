package com.socioseer.zuul.config;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
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
@FeignClient(value = "socio-seer-authorization-service")
public interface AuthorizationService {
/**
 * 
 * @param queryMap
 * @return
 */
	@RequestMapping(value = "validate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response<Boolean>> haveAccessToResource(@RequestBody Map<String, String> queryMap);

}
