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

import com.socioseer.common.domain.model.Country;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.CountryService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Country.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "country", produces = MediaType.APPLICATION_JSON_VALUE)
public class CountryController {

	@Autowired
	private CountryService countryService;

	/**
	 * <b>Save Country</b>
	 * @param country	country json
	 * @return			returns Country
	 * <b></br>URL FOR API :</b>	/api/admin/country 
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Country>> saveCountry(@RequestBody Country country) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Country saved successfully", countryService.save(country)),
				HttpStatus.OK);
	}

	/**
	 * <b>Update Country</b>
	 * @param id		countryId
	 * @param country	country json
	 * @return			returns Country
	 * <b></br>URL FOR API :</b>	/api/admin/country/{countryId}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Country>> updateCountry(@PathVariable String id, @RequestBody Country country) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Country updated successfully",
				countryService.update(id, country)), HttpStatus.OK);
	}

	/**
	 * <b>Get All Country</b>
	 * @param query
	 * @param pageable
	 * @return			returns country list	
	 * <b></br>URL FOR API :</b>	/api/admin/country/all
	 */
	@RequestMapping(value = "all" ,method = RequestMethod.GET)
	public ResponseEntity<Response<List<Country>>> getAllCountries(
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Countries fetched successfully",
				countryService.getAllCountries(pageable, QueryParser.parse(query))), HttpStatus.OK);
	}

	/**
	 * <b>Get Country by Id</b>
	 * @param id	countryId
	 * @return		returns Country
	 * <b></br>URL FOR API :</b>	/api/admin/country/{countryId}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<Response<Country>> getCountryById(@PathVariable String id) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Countries fetched successfully", countryService.get(id)),
				HttpStatus.OK);
	}
}