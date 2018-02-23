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

import com.socioseer.common.domain.model.Licence;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.LicenceService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Licence .</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "licence", produces = MediaType.APPLICATION_JSON_VALUE)
public class LicenceController {

  @Autowired
  private LicenceService licenceService;

  /**
   * <b>Save Licence</b>
   * @param licence		licence json
   * @return			returns Licence
   * <b></br>URL FOR API :</b>	/api/admin/licence
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Licence>> saveCountry(@RequestBody Licence licence) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Licence saved successfully",
        licenceService.save(licence)), HttpStatus.OK);
  }

  /**
   * <b>Update Licence</b>
   * @param id		licenceId	
   * @param licence	licence json	
   * @return		returns Licence
   * <b></br>URL FOR API :</b>	/api/admin/licence/{licenceId}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Licence>> updateCountry(@PathVariable String id,
      @RequestBody Licence licence) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Licence updated successfully", licenceService.update(id, licence)), HttpStatus.OK);
  }

  /**
   * <b>Get All Licence</b>
   * @param query
   * @param pageable
   * @return			returns licence list
   * <b></br>URL FOR API :</b>	/api/admin/licence/all
   */
  @RequestMapping(value = "all",method = RequestMethod.GET)
  public ResponseEntity<Response<List<Licence>>> getLicences(@RequestParam(value = "q",
      required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Licences fetched successfully", licenceService.getAllLicences(pageable,
            QueryParser.parse(query))), HttpStatus.OK);
  }

  /**
   * <b>Get Licence by Id</b>
   * @param id	licenceId
   * @return	returns Licence
   * <b></br>URL FOR API :</b>	/api/admin/licence/{licenceId}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public ResponseEntity<Response<Licence>> getCountryById(@PathVariable String id) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Licences fetched successfully", licenceService.get(id)), HttpStatus.OK);
  }
}
