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

import com.socioseer.common.domain.model.AudienceType;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.AudienceService;
import com.socioseer.restapp.service.api.AudienceTypeService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of AudienceType.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "audience-types", produces = MediaType.APPLICATION_JSON_VALUE)
public class AudienceTypeController {

  @Autowired
  private AudienceTypeService audienceTypeService;
  
  /**
   * <b>Save AudienceType</b>
   * @param audienceType	AudienceType Json 
   * @return				returns AudienceType
   * <b></br>URL FOR API :</b>	/api/admin/audience-types    
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<AudienceType>> saveAudienceType(
      @RequestBody AudienceType audienceType) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Audience type  saved successfully.", audienceTypeService.save(audienceType)),
        HttpStatus.OK);
  }

  /**
   * <b>Update AudienceType </b>
   * @param id				audienceTypeId 
   * @param audienceType	AudienceType Json
   * @return				returns AudienceType 
   * <b></br>URL FOR API :</b>	/api/admin/audience-types/{id}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<AudienceType>> updateAudienceType(@PathVariable("id") String id,
      @RequestBody AudienceType audienceType) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Audience type  updated successfully.", audienceTypeService.update(id, audienceType)),
        HttpStatus.OK);
  }

  /**
   * <b>Get AudienceType</b>
   * @param id	audienceTypeId 
   * @return	returns AudienceType 
   * <b></br>URL FOR API :</b>	/api/admin/audience-types/{id}	
   */
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public ResponseEntity<Response<AudienceType>> updateAudienceType(@PathVariable("id") String id) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Audience type  fetched successfully.", audienceTypeService.get(id)), HttpStatus.OK);
  }
  
  /**
   * <b>Get all AudienceType</b>
   * @param query
   * @param pageable
   * @return			returns AudienceType List 
   * <b></br>URL FOR API :</b>	/api/admin/audience-types/all	
   */
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<AudienceType>>> getAllAudienceTypes(
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Audience types  fetched successfully.",
            audienceTypeService.getAllAudienceTypes(pageable, QueryParser.parse(query)),
            audienceTypeService.getAllAudienceTypes(null, QueryParser.parse(query)).size()),
        HttpStatus.OK);
  }

  /**
   * <b>Delete AudienceType </b>
   * @param id			audienceTypeId as String
   * @param updatedBy	updatedBy as String 
   * @return			returns Boolean Data 
   * <b></br>URL FOR API :</b>	/api/admin/audience-types/delete/{id}/{updatedBy}	
   */
  @RequestMapping(value = "delete/{id}/{updatedBy}", method = RequestMethod.DELETE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("updatedBy") String updatedBy) {
    audienceTypeService.delete(id, updatedBy);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Audience Type deleted successfully.", true),
        HttpStatus.OK);
  }
}