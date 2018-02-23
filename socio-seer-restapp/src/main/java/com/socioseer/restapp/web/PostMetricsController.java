package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socioseer.common.domain.model.PostMetrics;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.PostMetricsService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of PostMetrics. </h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "postMetrics", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostMetricsController {

	@Autowired
	private PostMetricsService postMetricsService;

	/**
	 * <b>Save PostMetrics</b>
	 * @param postMetricsList
	 *            List of PostMetrics Details in Json format
	 * @return returns List of PostMetrics Object
	 * <b></br>URL FOR API :</b> /api/admin/postMetrics
	 */
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Response<List<PostMetrics>>> savePostMetrics(@RequestBody List<PostMetrics> postMetricsList) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Post Metrics saved successfully",
				postMetricsService.save(postMetricsList)), HttpStatus.OK);
	}

	/**
	 * <b>Get All PostMetrics</b>
	 * @param query
	 * @param pageable
	 * 
	 * @return returns List of PostMetrics Object
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET)
	public ResponseEntity<Response<List<PostMetrics>>> getAllUsers(
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		List<Filter> filters = QueryParser.parse(query);
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Postmetrics fetched successfully",
						postMetricsService.getAll(pageable, filters), postMetricsService.getAll(null, filters).size()),
				HttpStatus.OK);
	}

}
