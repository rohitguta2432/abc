package com.socioseer.authentication.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.socioseer.authentication.service.api.AuthenticationService;
import com.socioseer.authentication.service.api.ForgotPasswordService;
import com.socioseer.common.domain.AuthenticationToken;
import com.socioseer.common.domain.User;
import com.socioseer.common.dto.Response;

/**
 * <h3>This Controller Manage the All API of Authentication.</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private ForgotPasswordService forgotPasswordService;

	/**
	 * <b>Generate Authentication Token</b>
	 * @param user
	 * @return returns AuthenticationToken
	 * 
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<AuthenticationToken>> generateAuthToken(@RequestBody User user) {
		return new ResponseEntity<Response<AuthenticationToken>>(
				new Response<AuthenticationToken>(HttpStatus.OK.value(), "Authentication token generated successfully.",
						authenticationService.generateToken(user.getEmail(), user.getPassword(), null)),
				HttpStatus.OK);
	}

	/**
	 * <b>Validate Authentication Token</b>
	 * @param authToken
	 * @return returns Map<String, Object>
	 * <b></br>URL FOR API :</b>  /{authToken:.+}
	 */
	@RequestMapping(value = "{authToken:.+}", method = RequestMethod.POST)
	public ResponseEntity<Response<Map<String, Object>>> validateAuthToken(
			@PathVariable(name = "authToken", required = true) String authToken) {
		return new ResponseEntity<Response<Map<String, Object>>>(
				new Response<Map<String, Object>>(HttpStatus.OK.value(), "Authentication token validated successfully.",
						authenticationService.validateToken(authToken)),
				HttpStatus.OK);
	}

	/**
	 * <b>Delete Authentication Token</b>
	 * @param userId
	 * @return returns Boolean
	 * <b></br>URL FOR API :</b> /{userId}
	 */
	@RequestMapping(value = "{userId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response<Boolean>> deleteAuthToken(
			@PathVariable(name = "userId", required = true) String userId) {
		authenticationService.deleteAuthenticationTokenByUserId(userId);
		return new ResponseEntity<Response<Boolean>>(
				new Response<Boolean>(HttpStatus.OK.value(), "Authentication token deleted successfully.", null),
				HttpStatus.OK);
	}

	/**
	 * <b>Validate Token</b>
	 * @param token
	 * @return returns Boolean
	 * <b></br>URL FOR API :</b> /validatetoken/{token}
	 */
	@RequestMapping(value = "validatetoken/{token}", method = RequestMethod.POST)
	public ResponseEntity<Response<Boolean>> validateToken(@PathVariable(value = "token") String token) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Token validated successfully.",
				forgotPasswordService.validateToken(token)), HttpStatus.OK);
	}

	/**
	 * <b>Forgot Password</b>
	 * @param map
	 * @return returns Boolean
	 * <b></br>URL FOR API :</b> /forgot/password
	 */
	@RequestMapping(value = "forgot/password", method = RequestMethod.POST)
	public ResponseEntity<Response<Boolean>> forgotpassword(@RequestBody Map<String, String> map) {
		authenticationService.forgotPassword(map.get("email"));
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Mail send successfully.", true),
				HttpStatus.OK);
	}

	/**
	 * <b>Reset Password</b>
	 * @param map
	 * @return returns Boolean
	 * <b></br>URL FOR API :</b> /reset/password
	 */
	@RequestMapping(value = "reset/password", method = RequestMethod.POST)
	public ResponseEntity<Response<Boolean>> resetPassword(@RequestBody Map<String, String> map) {
		authenticationService.restPassword(map);
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Password reset successfully.", true),
				HttpStatus.OK);
	}

}
