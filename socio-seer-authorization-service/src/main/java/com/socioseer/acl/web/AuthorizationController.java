package com.socioseer.acl.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.socioseer.acl.service.api.AuthorizationService;
import com.socioseer.common.dto.Response;

/**
 * <h3>This Controller Manage the All API of Authorization.</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorizationController {

	@Autowired
	private AuthorizationService authorizationService;

	/**
	 * <b>Validate Resource</b>
	 * @param queryMap
	 * @return returns Boolean
	 * <b></br>URL FOR API :</b> /validate
	 */
	@RequestMapping(value = "validate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Boolean>> validateResourceAccess(
			@RequestBody Map<String, String> queryMap) {
		return new ResponseEntity<Response<Boolean>>(
				new Response<Boolean>(HttpStatus.OK.value(), "Validation success.",
						authorizationService.hasAccessToRequestedResource(queryMap)), HttpStatus.OK);
	}

	/**
	 * <b>Reload url Cache</b>
	 * <b></br>URL FOR API :</b> reload-url-cache 
	 */
	@RequestMapping(value = "reload-url-cache")
	public void reloadUrlCache() {
		authorizationService.reloadAuthorizationMap();
	}

}
