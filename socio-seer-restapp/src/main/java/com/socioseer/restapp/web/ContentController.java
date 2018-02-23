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

import com.socioseer.common.domain.model.campaign.Content;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.ContentService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Content.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "content", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContentController {

  @Autowired
  ContentService contentService;

  /**
   * <b>Save Content</b>
   * @param content   content json
   * @return		  returns Content object	
   * <b></br>URL FOR API :</b>	/api/admin/content 
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Content>> saveRole(@RequestBody Content content) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Content saved successfully",
        contentService.save(content)), HttpStatus.OK);
  }

  /**
   * <b>Get Content by contentId</b>
   * @param contentId	
   * @return			returns Content
   * <b></br>URL FOR API :</b>	/api/admin/content/{contentId}
   */
  @RequestMapping(value = "{contentId}", method = RequestMethod.GET)
  public ResponseEntity<Response<Content>> getContent(@PathVariable String contentId) {
    return new ResponseEntity<Response<Content>>(new Response<Content>(HttpStatus.OK.value(),
        "Content fetched successfully", contentService.get(contentId)), HttpStatus.OK);
  }

  /**
   * <b>Get All Content</b>
   * @param query
   * @param pageable
   * @return			returns Content list
   * <b></br>URL FOR API :</b>	/api/admin/content/all
   */
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Content>>> getAllContents(@RequestParam("q") String query,
      Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "All Contents fetched successfully",
            contentService.getAllContent(pageable, QueryParser.parse(query)),
            contentService.getAllContent(null, QueryParser.parse(query)).size()),
        HttpStatus.OK);
  }

  /**
   * <b>Update Content</b>
   * @param contentId
   * @param content		content json
   * @return			returns Content	
   * <b></br>URL FOR API :</b>	/api/admin/content/{contentId}			
   */
  @RequestMapping(value = "{contentId}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Content>> updateContent(@PathVariable String contentId,
      @RequestBody Content content) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Content updated successfully", contentService.update(contentId, content)), HttpStatus.OK);
  }

  /**
   * <b>Get Content by clientId</b>
   * @param id		clientId
   * @param query
   * @param pageable
   * @return			returns Content list
   * <b></br>URL FOR API :</b>	/api/admin/content/client/{clientId}
   */
  @RequestMapping(value = "client/{id}", method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<List<Content>>> getAllContentsByClientId(
      @PathVariable("id") String id, @RequestParam(value = "q", required = false) String query,
      Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Content fetched successfully",
            contentService.getAllContentsByClientId(id, QueryParser.parse(query), pageable),
            contentService.getAllContentsByClientId(id, QueryParser.parse(query), null).size()),
        HttpStatus.OK);
  }

  /**
   * <b>Get Content by userId</b>
   * @param id			userId
   * @param query
   * @param pageable
   * @return			returns Content list
   * <b></br>URL FOR API :</b>	/api/admin/content/user/{userId}
   */
  @RequestMapping(value = "user/{id}", method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<List<Content>>> getAllContentsByUserId(
      @PathVariable("id") String id, @RequestParam(value = "q", required = false) String query,
      Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Content fetched successfully",
            contentService.getAllContentsByUserId(id, QueryParser.parse(query), pageable),
            contentService.getAllContentsByUserId(id, QueryParser.parse(query), null).size()),
        HttpStatus.OK);
  }

  /**
   * <b>Delete Content</b>
   * @param id			contentId
   * @param updatedBy
   * @return			returns boolean data
   * <b></br>URL FOR API :</b>	/api/admin/content/delete/{id}/{updatedBy}
   */
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
    contentService.delete(id, updatedBy);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Content deleted successfully.", true),
        HttpStatus.OK);
  }
}