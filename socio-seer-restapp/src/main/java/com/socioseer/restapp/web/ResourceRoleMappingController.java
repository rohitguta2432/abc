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

import com.socioseer.common.domain.ResourceRoleMapping;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.ResourceRoleMappingService;

/**
 * <h3>This Controller Manage the All API of ResourceRoleMapping. </h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "role-url-mapping", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResourceRoleMappingController {

	@Autowired
	private ResourceRoleMappingService resourceRoleMappingService;

	/**
	 * <b>Save ResourceRoleMapping</b>
	 * @param resourceRoleMapping
	 *            ResourceRoleMapping Details in Json format
	 * @return returns ResourceRoleMapping Object
	 * <b></br>URL FOR API :</b> /api/admin/role-url-mapping
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<ResourceRoleMapping>> saveResourceRoleMappingService(
	      @RequestBody ResourceRoleMapping resourceRoleMapping) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
		"Resource Role Mapping saved successfully",
		resourceRoleMappingService.save(resourceRoleMapping)), HttpStatus.OK);
	}

	/**
	 * <b>Update ResourceRoleMapping</b>
	 * @param id
	 *            id as String
	 * @return returns ResourceRoleMapping Object
	 * <b></br>URL FOR API :</b> /api/admin/role-url-mapping/{id}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.PUT,
	      consumes = MediaType.APPLICATION_JSON_VALUE)
	  public ResponseEntity<Response<ResourceRoleMapping>> updateResourceRoleMappingService(
	      @PathVariable String id, @RequestBody ResourceRoleMapping resourceRoleMapping) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
		"Resource Role Mapping updated successfully", resourceRoleMappingService.update(id,
		    resourceRoleMapping)), HttpStatus.OK);
	  }

	/**
	 * <b>Get ResourceRoleMapping by id</b>
	 * @param id
	 *            id as String
	 * @return returns ResourceRoleMapping Object
	 * <b></br>URL FOR API :</b> /api/admin/role-url-mapping/{id}
	 */
	 @RequestMapping(value = "{id}", method = RequestMethod.GET)
	 public ResponseEntity<Response<ResourceRoleMapping>> getResourceRoleMappingById(
	      @PathVariable String id) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
		"Resource Role Mapping fetched successfully", resourceRoleMappingService.get(id)),
		HttpStatus.OK);
	  }

	/**
	 * <b>Get All ResourceRoleMapping</b>
	 * @return returns List of ResourceRoleMapping Object
	 * <b></br>URL FOR API :</b> /api/admin/role-url-mapping/all
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET)
	  public ResponseEntity<Response<List<ResourceRoleMapping>>> getAllResourceRoleMappingService() {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
		"Resource Role Mapping fetched successfully", resourceRoleMappingService.getAll()),
		HttpStatus.OK);
	  }

}
