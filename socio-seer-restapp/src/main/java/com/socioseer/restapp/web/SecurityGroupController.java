package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socioseer.common.domain.SecurityGroup;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.SecurityGroupService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of SecurityGroup.</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "security-group", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecurityGroupController {

	@Autowired
	private SecurityGroupService securityGroupService;

	/**
	 * <b>Save SecurityGroup</b>
	 * 
	 * @param securityGroup
	 *            SecurityGroup Details in Json format
	 * @return returns SecurityGroup Object
	 * <b></br>URL FOR API :</b> /api/admin/security-group
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Response<SecurityGroup>> saveSecurityGroup(
	      @RequestBody SecurityGroup securityGroup) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
		"Security group saved successfully.", securityGroupService.save(securityGroup)),
		HttpStatus.OK);
	  }

	/**
	 * <b>Update SecurityGroup</b>
	 * 
	 * @param securityGroupId
	 *            securityGroupId must be AlphaNumeric and pass as path
	 *            variable.
	 * @param securityGroupId
	 *            SecurityGroup Details in Json format
	 * @return returns the SecurityGroup Object
	 * <b></br>URL FOR API :</b> /api/admin/security-group/{securityGroupId}
	 */
	@RequestMapping(value = "{securityGroupId}", method = RequestMethod.PUT)
	  public ResponseEntity<Response<SecurityGroup>> updateSecurityGroup(
	      @PathVariable("securityGroupId") String securityGroupId,
	      @RequestBody SecurityGroup securityGroup) {
	    return new ResponseEntity<>(
		new Response<>(HttpStatus.OK.value(), "Security group updated successfully.",
		    securityGroupService.update(securityGroupId, securityGroup)),
		HttpStatus.OK);
	  }

	/**
	 * <b>Get SecurityGroup by Id</b>
	 * 
	 * @param securityGroupId
	 *            securityGroupId must be AlphaNumeric and pass as path
	 *            variable.
	 * @return returns the SecurityGroup Object
	 * <b></br>URL FOR API :</b> /api/admin/security-group/{securityGroupId}
	 */
	@RequestMapping(value = "{securityGroupId}", method = RequestMethod.GET)
	  public ResponseEntity<Response<SecurityGroup>> getSecurityGroupById(
	      @PathVariable("securityGroupId") String securityGroupId) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
		"Security groups fetched successfully.", securityGroupService.get(securityGroupId)),
		HttpStatus.OK);
	  }

	/**
	 * <b>Get All SecurityGroup</b>
	 * 
	 * @return returns the SecurityGroup Object
	 * <b></br>URL FOR API :</b> /api/admin/security-group/fetchall
	 */
	@RequestMapping(value = "/fetchall", method = RequestMethod.GET)
	  public ResponseEntity<Response<List<SecurityGroup>>> getAllSecurityGroups() {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
		"Security groups fetched successfully.", securityGroupService.getAllSecurityGroups()),
		HttpStatus.OK);
	  }

	/**
	 * <b>Get Security Groups by clientId</b>
	 * 
	 * @param clientId
	 *            clientId must be AlphaNumeric and pass as path variable.
	 * @param pageable
	 * @return returns the List of SecurityGroup Object.
	 * <b></br>URL FOR API :</b> /api/admin/security-group/client/{clientId}
	 */
	@RequestMapping(value = "client/{clientId}", method = RequestMethod.GET)
	  public ResponseEntity<Response<List<SecurityGroup>>> getSecurityGroupsByClientId(
	      @PathVariable("clientId") String clientId, Pageable pageable) {

	    return new ResponseEntity<>(
		new Response<>(HttpStatus.OK.value(), "Security groups fetched successfully by client id.",
		    securityGroupService.getSecurityGroupByClientId(clientId, pageable).get()),
		HttpStatus.OK);
	  }

	/**
	 * <b>Get All Active SecurityGroup</b>
	 * 
	 * @param query
	 * @param pageable
	 * @return returns the List of SecurityGroup Object.
	 * <b></br>URL FOR API :</b> /api/admin/security-group/fetchAllActive
	 */
	@RequestMapping(value = "/fetchAllActive", method = RequestMethod.GET)
	  public ResponseEntity<Response<List<SecurityGroup>>> getAllActiveSecurityGroups(
	      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
	    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
		"Security groups fetched successfully.",
		securityGroupService.getAllSecurityActiveGroups(pageable, QueryParser.parse(query)),
		securityGroupService.getAllSecurityActiveGroups(null, QueryParser.parse(query)).size()),
		HttpStatus.OK);
	  }

	/**
	 * <b>Change Status of SecurityGroup</b>
	 * 
	 * @param id
	 *            id must be AlphaNumeric and pass as path variable.
	 * @param status
	 *            status must be Integer in {1,2,3}
	 * @param updatedBy
	 *            updatedBy must be AlphaNumeric and pass as path variable.
	 * @return returns true if updated successfully
	 * <b></br>URL FOR API :</b>
	 *      /api/admin/security-group/status/{id}/{status}/{updatedBy}
	 */
	@RequestMapping(value = "status/{id}/{status}/{updatedBy}", method = RequestMethod.PUT)
	  public ResponseEntity<Response<Boolean>> changeStatus(@PathVariable("id") String id,
	      @PathVariable("status") int status, @PathVariable("updatedBy") String updatedBy) {
	    securityGroupService.changeStatus(id, status, updatedBy);
	    return new ResponseEntity<>(
		new Response<>(HttpStatus.OK.value(), "Security group status updated successfully.", true),
		HttpStatus.OK);
	  }

	/**
	 * <b>Get Security Group by name</b>
	 * 
	 * @param name
	 *            id must be AlphaNumeric and pass as path variable.
	 * @return returns SecurityGroup Object
	 * <b></br>URL FOR API :</b> /api/admin/security-group/securityGroup/name/{name}/
	 */
	@RequestMapping(value = "securityGroup/name/{name}/", method = RequestMethod.GET)
	  public ResponseEntity<Response<SecurityGroup>> getByName(@PathVariable("name") String name) {
	    return new ResponseEntity<>(
		new Response<>(HttpStatus.OK.value(), "Security groups fetch by name successfully.", securityGroupService.getByName(name)),
		HttpStatus.OK);
	  }

}
