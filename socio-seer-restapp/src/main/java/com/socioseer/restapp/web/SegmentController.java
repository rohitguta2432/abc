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

import com.socioseer.common.domain.model.Segment;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.SegmentService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Segment.</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "segment", produces = MediaType.APPLICATION_JSON_VALUE)
public class SegmentController {

	@Autowired
	private SegmentService segmentService;

	/**
	 * <b>Save Segment</b>
	 * 
	 * @param segment
	 *            Segment Details in Json format
	 * @return returns Segment Object
	 * <b></br>URL FOR API :</b> /api/admin/segment
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Segment>> saveIndustry(@RequestBody Segment segment) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Segment saved successfully", segmentService.save(segment)),
				HttpStatus.OK);
	}

	/**
	 * <b>Update Segment</b>
	 * 
	 * @param id
	 *            id must be AlphaNumeric and pass as path variable.
	 * @param segment
	 *            segment Details in Json format
	 * @return returns the Segment Object
	 * <b></br>URL FOR API :</b> /api/admin/segment/{id}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Segment>> updateSegment(@PathVariable String id, @RequestBody Segment segment) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "segment updated successfully",
				segmentService.update(id, segment)), HttpStatus.OK);
	}

	/**
	 * <b>Get All Segment</b>
	 * 
	 * @param query
	 * @param pageable
	 * @return returns the List of SubSegment Object if found.
	 * <b></br>URL FOR API :</b> /api/admin/segment/all
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET)
	public ResponseEntity<Response<List<Segment>>> getSegments(
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Segments fetched successfully",
				segmentService.getAllSegments(pageable, QueryParser.parse(query))), HttpStatus.OK);
	}

	/**
	 * <b>Get Segment By Id</b>
	 * 
	 * @param id
	 *            id must be AlphaNumeric and pass as path variable.
	 * @return returns the Segment Object if found.
	 * <b></br>URL FOR API :</b> /api/admin/segment/{id}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<Response<Segment>> getSegmentById(@PathVariable String id) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Segment fetched successfully", segmentService.get(id)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get Segment By industryId</b>
	 * 
	 * @param industryId
	 *            industryId must be AlphaNumeric and pass as path variable.
	 * @return returns the List of Segment Object.
	 * <b></br>URL FOR API :</b> /api/admin/segment/industry/{industryId}
	 */
	@RequestMapping(value = "industry/{industryId}", method = RequestMethod.GET)
	public ResponseEntity<Response<List<Segment>>> getSegmentByIndustryId(@PathVariable String industryId) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Segments fetched by industryId successfully",
				segmentService.getAllSegmentsByIndustryId(industryId)), HttpStatus.OK);
	}

}
