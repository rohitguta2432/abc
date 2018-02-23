package com.socioseer.restapp.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Client;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.util.JsonParser;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Client.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "client", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

  @Autowired
  private ClientService clientService;

  @Autowired
  private UserService userService;

  /**
   * <b>Save Client</b>
   * @param clientString	client json
   * @param logo			client multipart image file
   * @return
   * <b></br>URL FOR API :</b>	/api/admin/client 
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Response<Client>> saveClient(
      @RequestParam(value = "client") String clientString,
      @RequestParam(value = "logo", required = false) MultipartFile logo) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Client saved successfully",
            clientService.save(JsonParser.toObject(clientString, Client.class), logo)),
        HttpStatus.OK);
  }
  
  /**
   * <b>Get User Detail at login Time</b>
   * @param userId
   * @return			returns User
   * <b></br>URL FOR API :</b>	/api/admin/client/login/{userId}
   */
  @RequestMapping(value = "login/{clientId}" , method = RequestMethod.GET)
  public ResponseEntity<Response<User>> getUserByIdForLogin(@PathVariable("userId") String userId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User fetched successfully.",
        userService.get(userId)), HttpStatus.OK);
  }

  /**
   * <b>Get Client by clientId</b>
   * @param clientId
   * @param request
   * @return			returns Client
   * <b></br>URL FOR API :</b>	/api/admin/client/{clientId}			
   */
  @RequestMapping(value = "{clientId}", method = RequestMethod.GET)
  public ResponseEntity<Response<Client>> getClientById(@PathVariable String clientId,
      HttpServletRequest request) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Client fetched successfully",
        clientService.get(clientId)), HttpStatus.OK);
  }
  
  /**
   * <b>Get Client by clientId at login time</b>
   * @param clientId
   * @param request
   * @return			returns Client
   * <b></br>URL FOR API :</b>	/api/admin/client/login/{clientId}/client
   */
  @RequestMapping(value = "login/{clientId}/client", method = RequestMethod.GET)
  public ResponseEntity<Response<Client>> getLongClientById(@PathVariable String clientId,
      HttpServletRequest request) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Client fetched successfully",
        clientService.get(clientId)), HttpStatus.OK);
  }

  /**
   * <b>Get All Client</b>
   * @param query
   * @param pageable
   * @return
   * <b></br>URL FOR API :</b>	/api/admin/client/all
   */
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Client>>> getAllClients(
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Clients fetched successfully",
        clientService.getAllClients(pageable, QueryParser.parse(query)),
        clientService.getAllClients(null, QueryParser.parse(query)).size()), HttpStatus.OK);

  }

  /**
   * <b>Save User for Client</b>
   * @param users	users json in list 
   * @return		returns users list
   * <b></br>URL FOR API :</b>	/api/admin/client/user
   */
  @RequestMapping(value = "user", method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<List<User>>> saveUser(@RequestBody List<User> users) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "User saved successfully",
        userService.createUserForClientAccount(users)), HttpStatus.OK);
  }

  /**
   * <b>Update Client</b>
   * @param clientId		
   * @param clientString	client json
   * @param logo			client logo 			
   * @return			returns Client
   * <b></br>URL FOR API :</b>	/api/admin/client/{clientId}
   */
  @RequestMapping(value = "{clientId}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Client>> update(@PathVariable("clientId") String clientId,
      @RequestPart(value = "client", required = false) String clientString,
      @RequestParam(value = "logo", required = false) MultipartFile logo) {
    if (StringUtils.isEmpty(clientString)) {
      clientString = "{}";
    }
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Client updated successfully.",
            clientService.update(clientId, JsonParser.toObject(clientString, Client.class), logo)),
        HttpStatus.OK);
  }

  /**
   * <b>Delete Client</b>
   * @param id			clientId
   * @param updatedBy
   * @return			returns boolean data
   * <b></br>URL FOR API :</b>	/api/admin/client/delete/{id}/{updatedBy}
   */
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
    clientService.delete(id, updatedBy);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Client deleted successfully.", true), HttpStatus.OK);
  }

  /**
   * <b>Change status of Client</b>
   * @param id			clientId
   * @param status		status as integer
   * @param updatedBy
   * @return			returns Boolean data
   * <b></br>URL FOR API :</b>	/api/admin/client/status/{id}/{status}/{updatedBy}
   */
  @RequestMapping(value = "status/{id}/{status}/{updatedBy}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Boolean>> changeStatus(@PathVariable("id") String id,
      @PathVariable("status") int status, @PathVariable("updatedBy") String updatedBy) {
    clientService.changeStatus(id, status, updatedBy);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Client status updated successfully.", true),
        HttpStatus.OK);
  }
}
