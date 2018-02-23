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

import com.socioseer.common.domain.model.Currency;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.CurrencyService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Currency.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "currency", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrencyController {

  @Autowired
  private CurrencyService currencyService;

  /**
   * <b>Save Currency</b>
   * @param currency	currency json
   * @return			returns Currency	
   * <b></br>URL FOR API :</b>	/api/admin/currency
   */
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Currency>> saveCountry(@RequestBody Currency currency) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Currency saved successfully",
        currencyService.save(currency)), HttpStatus.OK);
  }

  /**
   * <b>Get Currency by Id</b>	
   * @param id	currencyId
   * @return	returns Currency	
   * <b></br>URL FOR API :</b>	/api/admin/currency/{currencyId}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.GET,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Currency>> get(@PathVariable("id") String id) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Currencies fetched successfully", currencyService.get(id)), HttpStatus.OK);
  }

  /**
   * <b>Update Currency</b>
   * @param id			currencyId
   * @param currency	Currency json	
   * @return			returns Currency
   * <b></br>URL FOR API :</b>	/api/admin/currency/{currencyId}
   */
  @RequestMapping(value = "{id}", method = RequestMethod.PUT,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Currency>> update(@PathVariable("id") String id,
      @RequestBody Currency currency) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Currency updated successfully",currencyService.update(id, currency) ),
        HttpStatus.OK);
  }

  /**
   * <b>Get All Currency</b>
   * @param query
   * @param pageable
   * @return			returns Currency list
   * <b></br>URL FOR API :</b>	/api/admin/currency/all
   */
  @RequestMapping(value = "all" ,method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<List<Currency>>> getAll(
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Currencies fetched successfully",
            currencyService.getAllCurrencies(pageable, QueryParser.parse(query)),currencyService.count()),
        HttpStatus.OK);
  }

  /**
   * <b>Delete Currency</b>
   * @param id			currencyId
   * @param deletedby
   * @return			returns boolean data
   * <b></br>URL FOR API :</b>	/api/admin/currency/{currencyId}/{deletedBy}
   */
  @RequestMapping(value = "{id}/{deletedby}", method = RequestMethod.DELETE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") String id,
      @PathVariable("deletedby") String deletedby) {
    currencyService.delete(id, deletedby);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Currency deleted successfully", true),
        HttpStatus.OK);
  }
}