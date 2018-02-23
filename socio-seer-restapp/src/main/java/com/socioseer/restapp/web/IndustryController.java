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

import com.socioseer.common.domain.model.Industry;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.IndustryService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Industry.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "industry", produces = MediaType.APPLICATION_JSON_VALUE)
public class IndustryController {

  @Autowired
  private IndustryService industryService;

  /**
   * <b>Save Industry</b>
   * @param industry	industry json	
   * @return			returns Industry
   * <b></br>URL FOR API :</b>	/api/admin/industry
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Industry>> saveIndustry(@RequestBody Industry industry) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "industry saved successfully", industryService.save(industry)), HttpStatus.OK);
  }

  /**
   * <b>Update Industry</b>
   * @param id			industryId
   * @param industry	industry json
   * @return			returns Industry
   * <b></br>URL FOR API :</b>	/api/admin/industry/{industryId}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Industry>> updateIndustry(@PathVariable String id,
      @RequestBody Industry industry) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "industry updated successfully", industryService.update(id, industry)), HttpStatus.OK);
  }

  /**
   * <b>Get All Industry</b>
   * @param query
   * @param pageable
   * @return			returns Industry list
   * <b></br>URL FOR API :</b>	/api/admin/industry/all
   */
  @RequestMapping(value = "all" ,method = RequestMethod.GET)
  public ResponseEntity<Response<List<Industry>>> getAllIndustries(@RequestParam(value = "q",
      required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Industries fetched successfully", industryService.getAllIndustries(pageable,
            QueryParser.parse(query))), HttpStatus.OK);
  }

  /**
   * <b>Get Industry by industryId</b>
   * @param id	industryId
   * @return	returns Industry
   * <b></br>URL FOR API :</b>	/api/admin/industry/{industryId}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public ResponseEntity<Response<Industry>> getIndustryById(@PathVariable String id) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "industries fetched successfully", industryService.get(id)), HttpStatus.OK);
  }
}
