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

import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.SocialHandlerService;
import com.socioseer.restapp.util.QueryParser;


/**
 * <h3>This Controller Manage the All API of SocialHandler.</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "social-handler", produces = MediaType.APPLICATION_JSON_VALUE)
public class SocialHandlerController {

  @Autowired
  private SocialHandlerService socialHandlerService;


  /*
   * @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
   * public ResponseEntity<Response<SocialHandler>> saveSocialHandler(
   * 
   * @RequestBody SocialHandler socialHandler) { return new ResponseEntity<>(new
   * Response<>(HttpStatus.OK.value(), "Social handle saved successfully.",
   * socialHandlerService.save(socialHandler)), HttpStatus.OK); }
   */

  /**
	 * <b>Save SocialHandler</b>
	 * 
	 * @param socialHandlers
	 *            SocialHandler Details in Json format
	 * @return returns SocialHandler Object
	 * <b></br>URL FOR API :</b> /api/admin/social-handler
	 */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<List<SocialHandler>>> saveSocialHandlers(
      @RequestBody List<SocialHandler> socialHandlers) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Social handle saved successfully.", socialHandlerService.save(socialHandlers)),
        HttpStatus.OK);
  }

  /**
	 * <b>Update SocialHandler</b>
	 * 
	 * @param id
	 *            id must be AlphaNumeric and pass as path
	 *            variable.
	 * @param socialHandler
	 *            socialHandler Details in Json format
	 * @return returns the SocialHandler Object
	 * <b></br>URL FOR API :</b> /api/admin/social-handler/{id}
	 */
  @RequestMapping(value = "{id}", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<SocialHandler>> updateSocialHandler(@PathVariable("id") String id,
      @RequestBody SocialHandler socialHandler) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Social handle updated successfully.", socialHandlerService.update(id, socialHandler)),
        HttpStatus.OK);
  }

  /**
	 * <b>Get SocialHandler by Id</b>
	 * 
	 * @param id
	 *            id must be AlphaNumeric and pass as path
	 *            variable.
	 * @return returns the SocialHandler Object
	 * <b></br>URL FOR API :</b> /api/admin/social-handler/{id}
	 */
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public ResponseEntity<Response<SocialHandler>> getSocialHandler(@PathVariable("id") String id) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Social handle fetched successfully.", socialHandlerService.get(id)), HttpStatus.OK);
  }

  /**
	 * <b>Get SocialHandler by clientId</b>
	 * 
	 * @param clientId
	 *            clientId must be AlphaNumeric and pass as path
	 *            variable.
	 * @param pageable
	 * @return returns the List of SocialHandlers Object
	 * <b></br>URL FOR API :</b> /api/admin/social-handler/client/{clientId}
	 */
  @RequestMapping(value = "client/{clientId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<SocialHandler>>> getSocialHandlerByClientId(
      @PathVariable("clientId") String clientId, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Social handles fetched successfully by client id.", socialHandlerService.getSocialHandlerByClientId(
            clientId, pageable)), HttpStatus.OK);
  }
  
  /**
	 * <b>Get All SocialHandlers</b>
	 * 
	 * @param query
	 * @param pageable
	 * @return returns the List of SocialHandlers Object
	 *<b></br>URL FOR API :</b> /api/admin/social-handler/all
	 */
//TODO: remove after actual login with social media
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<SocialHandler>>> getAll(@RequestParam(value = "q", required = false) String query,
          Pageable pageable) {
      return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Social handles fetched successfully.",
          socialHandlerService.getAll(pageable, QueryParser.parse(query)),socialHandlerService.getAll(null, QueryParser.parse(query)).size()), HttpStatus.OK);
  }

  /**
	 * <b>Get SocialHandler By clientId and platformId</b>
	 * 
	 * @param clientId  clientId as String
	 * @param platformId platformId as String
	 * @param pageable
	 * @return returns the List of SocialHandlers Object
	 * <b></br>URL FOR API :</b> /api/admin/social-handler/client/{clientId}/{platformId}
	 */
  @RequestMapping(value = "client/{clientId}/{platformId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<SocialHandler>>> getSocialHandlerByClientId(
      @PathVariable("clientId") String clientId, @PathVariable("platformId") String platformId, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Social handles fetched successfully.", socialHandlerService.getSocialHandlerByClientIdAndPostId(
            clientId, platformId, pageable)), HttpStatus.OK);
  }
  
  /**
	 * <b>Delete SocialHandler by Id</b>
	 * 
	 * @param id          id as String
	 * @param updatedBy   updatedBy as String
	 * @return returns true if deleted successfully.
	 * <b></br>URL FOR API :</b> /api/admin/social-handler/delete/{id}/{updatedBy}
	 */
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
      socialHandlerService.delete(id, updatedBy);
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Social handle deleted successfully.",
        true), HttpStatus.OK);
  }


}
