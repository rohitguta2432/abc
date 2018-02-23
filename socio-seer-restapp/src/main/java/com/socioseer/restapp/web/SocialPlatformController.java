package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.socioseer.common.domain.model.campaign.SocialPlatform;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.SocialPlatformService;

/**
 * <h3>This Controller Manage the All API of SocialPlateforms.</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "social-platforms", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SocialPlatformController {

	@Autowired
	private SocialPlatformService socialPlatformService;

	/**
	 * <b>Save SocialPlatform</b>
	 * 
	 * @param socialPlatform
	 *            SocialPlatform Details in Json format
	 * @return returns SocialPlatform Object
	 * <b></br>URL FOR API :</b> /api/admin/social-platforms
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<SocialPlatform>> saveSocialPlatform(@RequestBody SocialPlatform socialPlatform) {
		return new ResponseEntity<Response<SocialPlatform>>(new Response<SocialPlatform>(HttpStatus.OK.value(),
				"SocialPlatform saved successfully.", socialPlatformService.save(socialPlatform)), HttpStatus.OK);
	}

	/**
	 * <b>Update SocialPlatform</b>
	 * 
	 * @param socialPlatformId
	 *            socialPlatformId must be AlphaNumeric and pass as path
	 *            variable.
	 * @param socialPlatform
	 *            socialPlatform Details in Json format
	 * @return returns the SocialPlatform Object
	 * <b></br>URL FOR API :</b> /api/admin/social-platforms/{socialPlatformId}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseEntity<Response<SocialPlatform>> saveBrand(@PathVariable("id") String socialPlatformId,
			@RequestBody SocialPlatform socialPlatform) {
		return new ResponseEntity<Response<SocialPlatform>>(new Response<SocialPlatform>(HttpStatus.OK.value(),
				"SocialPlatform updated successfully.", socialPlatformService.update(socialPlatformId, socialPlatform)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get SocialPlatform by Id</b>
	 * 
	 * @param socialPlatformId
	 *            socialPlatformId must be AlphaNumeric and pass as path
	 *            variable.
	 * @return returns the SocialPlatform Object if found.
	 * <b></br>URL FOR API :</b> /api/admin/social-platforms/{socialPlatformId}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<Response<SocialPlatform>> getById(@PathVariable("id") String socialPlatformId) {
		return new ResponseEntity<Response<SocialPlatform>>(new Response<SocialPlatform>(HttpStatus.OK.value(),
				"SocialPlatform fetched successfully.", socialPlatformService.get(socialPlatformId)), HttpStatus.OK);
	}

	/**
	 * <b>Get SocialPlatform by name</b>
	 * 
	 * @param socialPlatformName
	 *            socialPlatformName must be AlphaNumeric and pass as path
	 *            variable.
	 * @return returns the SocialPlatform Object if found.
	 * <b></br>URL FOR API :</b> /api/admin/social-platforms/name/{name}
	 */
	@RequestMapping(value = "name/{name}", method = RequestMethod.GET)
	public ResponseEntity<Response<SocialPlatform>> getBySocialPlatformName(
			@PathVariable("name") String socialPlatformName) {
		return new ResponseEntity<Response<SocialPlatform>>(new Response<SocialPlatform>(HttpStatus.OK.value(),
				"SocialPlatform fetched successfully.", socialPlatformService.getPlatformByName(socialPlatformName)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get All SocialPlatforms</b>
	 * 
	 * @return returns the List of SocialPlatform Object.
	 * <b></br>URL FOR API :</b> /api/admin/social-platforms/name/{name}
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET)
	public ResponseEntity<Response<List<SocialPlatform>>> getAll() {
		return new ResponseEntity<Response<List<SocialPlatform>>>(
				new Response<List<SocialPlatform>>(HttpStatus.OK.value(), "SocialPlatform fetched successfully.",
						socialPlatformService.getAllPlatforms()),
				HttpStatus.OK);
	}

}
