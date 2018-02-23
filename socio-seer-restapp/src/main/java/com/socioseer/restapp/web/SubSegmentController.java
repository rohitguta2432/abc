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

import com.socioseer.common.domain.model.SubSegment;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.SubSegmentService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of SubSegment.</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "subsegment", produces = MediaType.APPLICATION_JSON_VALUE)
public class SubSegmentController {

	@Autowired
	private SubSegmentService subSegmentService;

	/**
	 * <b>Save SubSegment</b>
	 * 
	 * @param subSegment
	 *            SubSegment Details in Json format
	 * @return returns SubSegment Object
	 * <b></br>URL FOR API :</b> /api/admin/subsegment
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<SubSegment>> saveIndustry(@RequestBody SubSegment subSegment) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "SubSegment saved successfully",
				subSegmentService.save(subSegment)), HttpStatus.OK);
	}

	/**
	 * <b>Update SubSegment</b>
	 * 
	 * @param id
	 *            id must be AlphaNumeric and pass as path variable.
	 * @param subSegment
	 *            subSegment Details in Json format
	 * @return returns the SubSegment Object
	 * <b></br>URL FOR API :</b> /api/admin/subsegment/{id}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<SubSegment>> updateSubSegment(@PathVariable String id,
			@RequestBody SubSegment subSegment) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "subSegment updated successfully",
				subSegmentService.update(id, subSegment)), HttpStatus.OK);
	}

	/**
	 * <b>Get All SubSegment</b>
	 * 
	 * @param query
	 * @param pageable
	 * @return returns the List of SubSegment Object if found.
	 * <b></br>URL FOR API :</b> /api/admin/subsegment/all
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET)
	public ResponseEntity<Response<List<SubSegment>>> getSubSegments(
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "SubSegments fetched successfully",
				subSegmentService.getAllSubSegments(pageable, QueryParser.parse(query))), HttpStatus.OK);
	}

	/**
	 * <b>Get SubSegment by Id</b>
	 * 
	 * @param id
	 *            id must be AlphaNumeric and pass as path variable.
	 * @return returns the SubSegment Object if found.
	 * <b></br>URL FOR API :</b> /api/admin/subsegment/{id}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<Response<SubSegment>> getSubSegmentById(@PathVariable String id) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "SubSegment fetched successfully", subSegmentService.get(id)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get SubSegment By segmentId</b>
	 * 
	 * @param segmentId
	 *            segmentId must be AlphaNumeric and pass as path variable.
	 * @return returns the List of SubSegment Object if found.
	 * <b></br>URL FOR API :</b> /api/admin/subsegment/segment/{segmentId}
	 */
	@RequestMapping(value = "segment/{segmentId}", method = RequestMethod.GET)
	public ResponseEntity<Response<List<SubSegment>>> getSubSegmentBySegmentId(@PathVariable String segmentId) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "SubSegments fetched successfully",
				subSegmentService.getAllSubSegmentsBySegmentId(segmentId)), HttpStatus.OK);
	}
}
