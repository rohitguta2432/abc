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

import com.socioseer.common.domain.Role;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.RoleService;

/**
 * <h3>This Controller Manage the All API of Role. </h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleController {

	@Autowired
	private RoleService roleService;

	/**
	 * <b>Save Role</b>
	 * @param role
	 *            Role Details in Json format
	 * @return returns Role Object
	 * <b></br>URL FOR API :</b> /api/admin/roles
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Role>> saveRole(@RequestBody Role role) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Role saved successfully", roleService.save(role)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get Role by roleName</b>
	 * @param roleName
	 *            roleName must be AlphaNumeric and pass as path.
	 * @return returns Role Object
	 * <b></br>URL FOR API :</b> /api/admin/roles/{roleName}
	 */
	@RequestMapping(value = "{roleName}", method = RequestMethod.GET)
	public ResponseEntity<Response<Role>> getRoleByName(@PathVariable String roleName) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Role fetched successfully", roleService.getRoleByName(roleName)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get Role by roleId</b>
	 * @param roleId
	 *            roleId must be AlphaNumeric and pass as path.
	 * @return returns Role Object
	 * <b></br>URL FOR API :</b> /api/admin/roles/{roleId}
	 */
	@RequestMapping(value = "{roleId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Role>> updateRole(@PathVariable String roleId, @RequestBody Role role) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Role updated successfully", roleService.update(roleId, role)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get All Role</b>
	 * @return returns List of Roles
	 * <b></br>URL FOR API :</b> /api/admin/roles/all
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET)
	public ResponseEntity<Response<List<Role>>> getAllRoles() {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Roles fetched successfully",
				roleService.getAllRoles(), roleService.countAll()), HttpStatus.OK);
	}

	/**
	 * <b>Delete Role</b>
	 * @param id
	 *            id must be AlphaNumeric and pass as path.
	 * @param updatedBy
	 *            updatedBy must be AlphaNumeric and pass as path.
	 * @return returns List of Roles
	 * <b></br>URL FOR API :</b> /api/admin/roles/delete/{id}/{updatedBy}
	 */
	@RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
	public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
			@PathVariable("updatedBy") String updatedBy) {
		roleService.delete(id, updatedBy);
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Role deleted successfully.", true),
				HttpStatus.OK);
	}

}
