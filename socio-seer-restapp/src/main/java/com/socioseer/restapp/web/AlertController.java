package com.socioseer.restapp.web;

import java.util.List;
import java.util.Map;

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

import com.socioseer.common.domain.model.Alert;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.AlertService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Notification.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "notification", produces = MediaType.APPLICATION_JSON_VALUE)
public class AlertController {

  @Autowired
  private AlertService alertService;

  @RequestMapping(value = "fb/token/expiration/{userId}", method = RequestMethod.GET)
  public ResponseEntity<Response<Map<String,String>>> validateToken(
      @PathVariable("userId") String userId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Notifications fetched successfully", alertService.validateFacebookToken(userId)),
        HttpStatus.OK);
  }

  /**
   * <b>Save Notification</b>
   * @param notification	Alert object	
   * @return				returns the Alert
   * <b></br>URL FOR API :</b> /api/admin/notification             
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Alert>> save(@RequestBody Alert notification) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Alert saved successfully",
        alertService.save(notification)), HttpStatus.OK);
  }

  /**
   * <b>Update Notification </b>
   * @param id 				id must be AlphaNumeric and pass as path variable.  
   * @param notification    Alert Object 
   * @return				returns the Alert
   * <b></br>URL FOR API :</b>	/api/admin/notification/{id}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Alert>> update(@PathVariable("id") String id,
      @RequestBody Alert notification) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Alert updated successfully",
        alertService.update(id, notification)), HttpStatus.OK);
  }

  /**
   * <b>Get Notification By Id </b>
   * @param id              id must be AlphaNumeric and pass as path variable.
   * @return				returns the Alert
   * <b></br>URL FOR API :</b>	/api/admin/notification/{id}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public ResponseEntity<Response<Alert>> getAlertById(@PathVariable("id") String id) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Alert fetched successfully",
        alertService.get(id)), HttpStatus.OK);
  }

  /**
   * <b>Get Notifications count of user</b>
   * @param userId  userId must be AlphaNumeric and pass as path variable.
   * @return		returns integer     
   * <b></br>URL FOR API :</b>	/api/admin/notification/count/{userId}
   */
  @RequestMapping(value = "count/{userId}", method = RequestMethod.GET)
  public ResponseEntity<Response<Integer>> getAlertCountByUserId(
      @PathVariable("userId") String userId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Alert fetched successfully",
        alertService.getCountByUserId(userId)), HttpStatus.OK);
  }

  /**
   * <b>Get All Notifications of user</b>
   * @param userId	userId must be AlphaNumeric and pass as path variable.
   * @param pageable
   * @param query
   * @return		returns Alert object     
   * <b></br>URL FOR API :</b> /api/admin/notification/get/user/{userId}
   */
  @RequestMapping(value = "get/user/{userId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Alert>>> getAlertByUserId(
      @PathVariable("userId") String userId, Pageable pageable, @RequestParam(value = "q",
          required = false) String query) {
    List<Filter> filters = QueryParser.parse(query);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Alert fetched successfully", alertService.getNotificationByUserId(userId,
            pageable, filters), alertService.getNotificationByUserId(userId, pageable, filters)
            .size()), HttpStatus.OK);
  }

  /**
   * <b>Update Notification by userId</b>
   * @param userId  userId must be AlphaNumeric and pass as path variable.
   * @return		returns Boolean     
   * <b></br>URL FOR API :</b>	/api/admin/notification/user/{userId}
   */
  @RequestMapping(value = "user/{userId}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Boolean>> updateALertStatus(@PathVariable("userId") String userId) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Alert status updated successfully",
            alertService.updateNotificationStatus(userId)), HttpStatus.OK);
  }

  /**
   * <b>Delete the Notification By Id</b>
   * @param id			id must be AlphaNumeric and pass as path variable.
   * @param updatedBy
   * @return		returns Boolean     
   * <b></br>URL FOR API :</b>	/api/admin/notification/delete/{id}/{updatedBy}
   */
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
    alertService.delete(id, updatedBy);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Alert deleted successfully.", true), HttpStatus.OK);
  }

  /**
   * <b>Returns the all notifications list </b>
   * @param query
   * @param pageable
   * @return			returns Alert list
   * <b></br>URL FOR API :</b>	/api/admin/notification/all	
   */
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Alert>>> getAllAlerts(@RequestParam(value = "q",
      required = false) String query, Pageable pageable) {
    List<Filter> filters = QueryParser.parse(query);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Alert fetched successfully",
        alertService.getAll(pageable, filters), alertService.getAll(null, filters).size()),
        HttpStatus.OK);
  }

  /**
   * <b>Returns the total counts of user notifications</b>
   * @param userId		userId must be AlphaNumeric and pass as path variable.     
   * @param pageable
   * @return			map object
   * <b></br>URL FOR API :</b> /api/admin/notification/get/user/{userId}/notification 	
   */
  @RequestMapping(value = "get/user/{userId}/notification", method = RequestMethod.GET)
  public ResponseEntity<Response<Map<String, Integer>>> getAllNotViewdByCategories(
      @PathVariable("userId") String userId, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Notifications fetched successfully", alertService.getAllNotViewed(userId, pageable)),
        HttpStatus.OK);
  }
}
