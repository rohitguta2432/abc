package com.socioseer.restapp.web;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.socioseer.common.domain.User;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.util.QueryParser;
import com.socioseer.restapp.util.JsonParser;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * <h3>This Controller Manage the All API of User.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

  @Autowired
  private UserService userService;

  /**
   * <b>Get User by userId</b>
   * @param  userId  userId must be AlphaNumeric and pass as path variable.  
   * @return returns the User Object if found.
   * <b></br>URL FOR API :</b>  /api/admin/user/{userId}       
   */
  @RequestMapping(value = "{userId}")
  public ResponseEntity<Response<User>> getUserById(@PathVariable("userId") String userId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User fetched successfully.",
        userService.get(userId)), HttpStatus.OK);
  }
  
  /**
   * <b>Get User By userId.<b> 
   * <b>This is uses only at Login Time</b>
   * @param userId          userId must be AlphaNumeric and pass as path variable.  
   * @return 				returns the User Object if found.
   * <b></br>URL FOR API :</b>  /api/admin/user/login/{userId}    
   */
  @RequestMapping(value = "login/{userId}" , method = RequestMethod.GET)
  public ResponseEntity<Response<User>> getUserByIdForLogin(@PathVariable("userId") String userId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User fetched successfully.",
        userService.get(userId)), HttpStatus.OK);
  }

  /**
   * <b>Save User</b>
   * @param  userStrings                  user Details in Json format as String
   * @param  logos                        multipart File of User Image
   * @return                              returns User Object in List  
   * @throws JsonParseException            
   * @throws JsonMappingException
   * @throws JsonProcessingException
   * @throws IOException
   * <b></br>URL FOR API :</b>   /api/admin/user/    
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Response<List<User>>> save(@RequestParam(value = "user")  String userStrings,
      @RequestParam(value = "logo", required = false) List<MultipartFile> logos) throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
	  List<User> users = JsonParser.getObject(userStrings, new TypeReference<List<User>>() {});
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User saved successfully.",
        userService.save(users, logos)), HttpStatus.OK);
  }

  /**
   * <b>Get users list by client Id</b>
   * @param clientId     clientId must be AlphaNumeric and pass as path variable. 
   * @param query         
   * @param pageable       
   * @return              returns the users list
   * <b></br>URL FOR API :</b>   /api/admin/user/client/{clientId}    
   */
  @RequestMapping(value = "client/{clientId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<User>>> getUsersByClientId(
      @PathVariable("clientId") String clientId,
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Users fetched successfully by client id.", userService.getUsersByClientId(clientId, pageable,
            QueryParser.parse(query))), HttpStatus.OK);
  }

  /**
   * <b>Get users list by brand Id</b>
   * @param brandId    brandId must be AlphaNumeric and pass as path variable. 
   * @param pageable    
   * @return           returns the users list
   * <b></br>URL FOR API :</b>     /api/admin/user/brand/{brandId}      
   */
  @RequestMapping(value = "brand/{brandId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<User>>> getbrandId(@PathVariable("brandId") String brandId,
      Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "User fetched successfully by brand id.", userService.getUsersByBrandId(brandId, pageable)
            .get()), HttpStatus.OK);
  }

  /**
   * <b>Update the user</b>
   * @param userId		userId must be AlphaNumeric and pass as path variable.
   * @param userString  user Details in Json format as String 
   * @param logo        multipart File of User Image
   * @return			returns the User 	
   * <b></br>URL FOR API :</b>  /api/admin/user/{userId} 
   */
  @RequestMapping(value = "{userId}", method = RequestMethod.PUT)
  public ResponseEntity<Response<User>> update(@PathVariable("userId") String userId, @RequestPart(
      value = "user", required = false) String userString, @RequestParam(value = "logo",
      required = false) MultipartFile logo) {
    if (StringUtils.isEmpty(userString)) {
      userString = "{}";
    }
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User updated successfully.",
        userService.update(userId, JsonParser.toObject(userString, User.class), logo)),
        HttpStatus.OK);
  }
  
  /**
   * <b>Get User Profile </b>
   * @param userId userId must be AlphaNumeric and pass as path variable.
   * @return returns the User
   * <b></br>URL FOR API :</b> /api/admin/user/manage/profile/{userId} 
   */
  @RequestMapping(value = "manage/profile/{userId}" , method = RequestMethod.GET)
  public ResponseEntity<Response<User>> getManageProfileUserById(@PathVariable("userId") String userId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User fetched successfully.",
        userService.get(userId)), HttpStatus.OK);
  }

  /**
   * <b>Update User Profile</b>
   * @param userId       userId must be AlphaNumeric and pass as path variable.
   * @param userString   user Details in Json format as String 
   * @param logo   		 multipart File of User Image
   * @return returns the User
   * <b></br>URL FOR API :</b> /api/admin/user/manage/profile/{userId} 
   */
  @RequestMapping(value = "manage/profile/{userId}", method = RequestMethod.PUT)
  public ResponseEntity<Response<User>> updateManageProfile(@PathVariable("userId") String userId, @RequestPart(
      value = "user", required = false) String userString, @RequestParam(value = "logo",
      required = false) MultipartFile logo) {
    if (StringUtils.isEmpty(userString)) {
      userString = "{}";
    }
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User updated successfully.",
        userService.update(userId, JsonParser.toObject(userString, User.class), logo)),
        HttpStatus.OK);
  }

  /**
   * <b>Get Users by clientId and status</b>
   * @param clientId      clientId must be AlphaNumeric and pass as path variable.
   * @param active        integer type data and pass as path variable.
   * @return		returns the users list
   * <b></br>URL FOR API :</b>  /api/admin/user/client/{clientId}/status/{status} 
   */
  @RequestMapping(value = "client/{clientId}/status/{status}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<User>>> getUserByclientIdAndStatus(
      @PathVariable("clientId") String clientId, @PathVariable("status") int active) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User fetched successfully.",
        userService.getUserByclientIdAndStatus(clientId, active).get()), HttpStatus.OK);
  }

  /**
   * <b>Get All Users List</b>
   * @param query
   * @param pageable      
   * @return returns the users list
   * <b></br>URL FOR API :</b>  /api/admin/user/all 
   */
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<User>>> getAllUsers(
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    List<Filter> filters = QueryParser.parse(query);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Users fetched successfully",
        userService.getAllUsers(pageable, filters),
        userService.getAllUsers(null, filters).size()), HttpStatus.OK);
  }

  /**
   * <b>Delete User by User Id</b>
   * @param id          userId must be AlphaNumeric and pass as path variable
   * @param updatedBy   userId(who going to delete the user) must be AlphaNumeric and pass as path variable
   * @return          returns boolean data
   * <b></br>URL FOR API :</b>  /api/admin/user/delete/{id}/{updatedBy}
   */
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
    userService.delete(id, updatedBy);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User deleted successfully.",
        true), HttpStatus.OK);
  }

  /**
   * <b>Change the User status </b>
   * @param id          userId must be AlphaNumeric and pass as path variable
   * @param status      integer value for user status
   * @param updatedBy   userId(who going to change status of user) must be AlphaNumeric and pass as path variable
   * @return			returns boolean data
   * <b></br>URL FOR API :</b>  /api/admin/user/status/{id}/{status}/{updatedBy}
   */
  @RequestMapping(value = "status/{id}/{status}/{updatedBy}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Boolean>> changeStatus(@PathVariable("id") String id,
      @PathVariable("status") int status, @PathVariable("updatedBy") String updatedBy) {
    userService.changeStatus(id, status, updatedBy);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "User status updated successfully.", true), HttpStatus.OK);
  }
  
  /**
   * <b>Get Approver By Client Id</b>
   * @param clientId   clientId must be AlphaNumeric and pass as path variable
   * @param query
   * @param pageable
   * @return returns users list
   * <b></br>URL FOR API :</b> /api/admin/user/approver/{clientId}			   
   */
  @RequestMapping(value = "approver/{clientId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<User>>> getApproversByClientId(
      @PathVariable("clientId") String clientId,
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Users fetched successfully.", userService.getApproversByClientId(clientId, pageable,
            QueryParser.parse(query))), HttpStatus.OK);
  }
}
