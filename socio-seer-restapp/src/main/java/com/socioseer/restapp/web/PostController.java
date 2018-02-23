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
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.domain.model.post.PostScheduleResponse;
import com.socioseer.common.domain.model.request.PostScheduleRequest;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.api.PostService;
import com.socioseer.restapp.util.QueryParser;


/**
 * <h3>This Controller Manage the All API of Post. </h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */

@RestController
@RequestMapping(value = "post", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private PostScheduleService postScheduleService;


	/**
	 * <b>Save Post</b>
	 * @param post
	 *            Post Details in Json format
	 * @return returns Post Object
	 * <b></br>URL FOR API :</b> /api/admin/post
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Post>> savePost(@RequestBody Post post) {
		return new ResponseEntity<Response<Post>>(
				new Response<Post>(HttpStatus.OK.value(), "Post saved successfully", postService.save(post)),
				HttpStatus.OK);
	}

	/**
	 * <b>Save PostSchedules</b>
	 * @param postId
	 *            postId must be AlphaNumeric and pass as path variable.
	 * @param postScheduleRequests
	 *            List of postScheduleRequests Detail in Json format
	 * @return returns List of PostSchedule Object
	 * <b></br>URL FOR API :</b> /api/admin/post/postSchedule/{postId}/schedule
	 */
	@RequestMapping(value = "postSchedule/{postId}/schedule", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<List<PostSchedule>>> schedulePost(@PathVariable("postId") String postId,
			@RequestBody List<PostScheduleRequest> postScheduleRequests) {
		return new ResponseEntity<Response<List<PostSchedule>>>(new Response<List<PostSchedule>>(HttpStatus.OK.value(),
				"Post scheduled successfully", postScheduleService.save(postId, postScheduleRequests)), HttpStatus.OK);
	}


	/**
	 * <b>Activate Post</b>
	 * @param postId
	 *            postId must be AlphaNumeric and pass as path variable.
	 * @param post
	 *            List of post Detail in Json format
	 * @return returns Post Object
	 * <b></br>URL FOR API :</b> /api/admin/post/{postId}/activate
	 */
	@RequestMapping(value = "{postId}/activate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Post>> setActivate(@PathVariable String postId, @RequestBody Post post) {
		return new ResponseEntity<Response<Post>>(new Response<Post>(HttpStatus.OK.value(),
				"Post activated successfully", postService.activatePost(postId, post)), HttpStatus.OK);
	}


	/**
	 * <b>Get Post by clientId</b>
	 * @param id
	 *            id must be AlphaNumeric and pass as path variable.
	 * @param query
	 * @param pageable
	 * @return returns Post Object
	 * <b></br>URL FOR API :</b> /api/admin/post/get/client/{id}
	 */
	@RequestMapping(value = "get/client/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<List<Post>>> getDraftPost(@PathVariable String id,
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		return new ResponseEntity<Response<List<Post>>>(new Response<List<Post>>(HttpStatus.OK.value(),
				"Draft Post fetched successfully", postService.getDraftPost(id, QueryParser.parse(query), pageable),
				postService.getDraftPost(id, QueryParser.parse(query), null).size()), HttpStatus.OK);
	}


	/**
	 * <b>Update Post</b>
	 * @param id
	 *            id must be AlphaNumeric and pass as path variable.
	 * @param post
	 *            post Detail in Json format
	 * @return returns Post Object
	 * <b></br>URL FOR API :</b> /api/admin/post/{id}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Post>> update(@PathVariable String id, @RequestBody Post post) {
		return new ResponseEntity<Response<Post>>(
				new Response<Post>(HttpStatus.OK.value(), "Post updated successfully", postService.update(id, post)),
				HttpStatus.OK);
	}


	/**
	 * <b>Delete Post</b>
	 * @param id
	 *            id must be AlphaNumeric and pass as path.
	 * @param deletedBy
	 *            deletedBy must be AlphaNumeric and pass as path variable.
	 * @return returns true if deleted successfully.
	 * <b></br>URL FOR API :</b> /api/admin/post/{id}/{deletedBy}
	 */
	@RequestMapping(value = "{id}/{deletedBy}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Boolean>> delete(@PathVariable String id, @PathVariable String deletedBy) {
		postService.delete(id, deletedBy);
		return new ResponseEntity<Response<Boolean>>(
				new Response<Boolean>(HttpStatus.OK.value(), "Post deleted successfully", true), HttpStatus.OK);
	}


	/**
	 * <b>Get All Post</b>
	 * @param query
	 * @param pageable
	 * @return returns List of POst Object
	 * <b></br>URL FOR API :</b> /api/admin/post/all
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET)
	public ResponseEntity<Response<List<Post>>> getAll(@RequestParam(value = "q", required = false) String query,
			Pageable pageable) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Posts fetched successfully.",
				postService.getAllPost(pageable, QueryParser.parse(query)),
				postService.getAllPost(null, QueryParser.parse(query)).size()), HttpStatus.OK);
	}


	/**
	 * <b>Get Post by postId</b>
	 * @param id
	 *            id must be AlphaNumeric and pass as path variable.
	 * @return returns Post Object
	 * <b></br>URL FOR API :</b> /api/admin/post/{id}
	 */
	@RequestMapping(value = "{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Post>> get(@PathVariable String id) {
		return new ResponseEntity<Response<Post>>(
				new Response<Post>(HttpStatus.OK.value(), "Post fetched successfully", postService.get(id)),
				HttpStatus.OK);
	}


	/**
	 * <b>Get All PostSchedule</b>
	 * @param query
	 * @param pageable
	 * @return returns List of PostSchedule Object
	 * <b></br>URL FOR API :</b> /api/admin/post/postSchedule/all
	 */
	@RequestMapping(value = "postSchedule/all", method = RequestMethod.GET)
	public ResponseEntity<Response<List<PostSchedule>>> postScheduleAll(
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "PostSchedule fetched successfully.",
				postService.getAllPostSchedules(pageable, QueryParser.parse(query)),
				postService.getAllPostSchedules(null, QueryParser.parse(query)).size()), HttpStatus.OK);
	}


	/**
	 * <b>Get PostSchedule by start date and status</b>
	 * @param startDate
	 *            startDate must be AlphaNumeric and pass as path variable.
	 * @param status
	 *            status must be AlphaNumeric and pass as path variable
	 * @param pageable
	 * @param query
	 * @return  returns List of PostSchedule Object
	 * <b></br>URL FOR API :</b> /api/admin/post/postSchedule/startDate/{startDate}/status/{status}
	 */
	@RequestMapping(value = "postSchedule/startDate/{startDate}/status/{status}", method = RequestMethod.GET)
	public ResponseEntity<Response<List<PostSchedule>>> postScheduleWithRange(
			@PathVariable(value = "startDate") String startDate, @PathVariable(value = "status") Boolean status,
			Pageable pageable, @RequestParam(value = "q", required = false) String query) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "PostSchedule fetched successfully.",
				postService.postScheduleWithRange(startDate, status, query, pageable),
				postService.postScheduleWithRange(startDate, status, query, null).size()), HttpStatus.OK);
	}


	/**
	 * <b>Get PostSchedule by start date,end date and status</b>
	 * @param startDate
	 *            startDate must be AlphaNumeric and pass as path variable.
	 * @param endDate
	 *            startDate must be AlphaNumeric and pass as path variable.
	 * @param status
	 *            status must be int and pass as path variable.
	 * @param clientId
	 *            clientId must be AlphaNumeric and pass as path variable.
	 * @param campaignId
	 *            campaignId must be AlphaNumeric and pass as path variable.
	 * @param pageable
	 * @param query
	 * @return  returns List of PostSchedule Object
	 * <b></br>URL FOR API :</b> /api/admin/post/postSchedule/startDate/{startDate}/endDate/{endDate}/status/{status}
	 */
	@RequestMapping(value = "postSchedule/startDate/{startDate}/endDate/{endDate}/status/{status}", method = RequestMethod.GET)
	public ResponseEntity<Response<List<PostSchedule>>> postScheduleWithDateRange(
			@PathVariable(value = "startDate") String startDate, @PathVariable(value = "endDate") String endDate,
			@PathVariable(value = "status") Boolean status,
			@RequestParam(value = "clientId", required = false) String clientId,
			@RequestParam(value = "campaignId", required = false) String campaignId, Pageable pageable,
			@RequestParam(value = "q", required = false) String query) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "PostSchedule fetched successfully.",
				postService.postScheduleWithRangeStartAndEnd(startDate, endDate, status, clientId, campaignId, pageable,
						query),
				postService
						.postScheduleWithRangeStartAndEnd(startDate, endDate, status, clientId, campaignId, null, query)
						.size()),
				HttpStatus.OK);
	}


	/**
	 * <b>Get PostSchedule by start date,status and clientId</b>
	 * @param startDate
	 *            startDate must be AlphaNumeric and pass as path variable.
	 * @param status
	 *            status must be AlphaNumeric and pass as path variable.
	 * @param clientId
	 *            clientId must be AlphaNumeric and pass as path variable.
	 * @param query
	 * @param pageable
	 * @return    returns List of PostSchedule Object
	 * <b></br>URL FOR API :</b> /api/admin/post/postSchedule/startDate/{startDate}/status/{status}/client/{clientId}
	 */
	@RequestMapping(value = "postSchedule/startDate/{startDate}/status/{status}/client/{clientId}", method = RequestMethod.GET)
	public ResponseEntity<Response<List<PostSchedule>>> postScheduleWithRangeWithClientId(
			@PathVariable(value = "startDate") String startDate, @PathVariable(value = "status") Boolean status,
			@PathVariable(value = "clientId") String clientId,
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "PostSchedule fetched successfully.",
						postService.postScheduleWithRangeByClientId(startDate, status, clientId, query, pageable),
						postService.postScheduleWithRangeByClientId(startDate, status, clientId, query, null).size()),
				HttpStatus.OK);
	}


	/**
	 * <b>Get PostSchedule by start date,end date,status and clientId</b>
	 * @param startDate
	 *            startDate must be AlphaNumeric and pass as path variable.
	 * @param endDate
	 *            endDate must be AlphaNumeric and pass as path variable.
	 * @param status
	 *            status must be int and pass as path variable.
	 * @param clientId
	 *            clientId must be AlphaNumeric and pass as path variable.
	 * @param pageable
	 * @return  returns List of PostSchedule Object
	 * <b></br>URL FOR API :</b> /api/admin/post/postSchedule/startDate/{startDate}/endDate/{endDate}/status/{status}/client/{clientId}
	 */
	@RequestMapping(value = "postSchedule/startDate/{startDate}/endDate/{endDate}/status/{status}/client/{clientId}", method = RequestMethod.GET)
	public ResponseEntity<Response<List<PostSchedule>>> postScheduleWithDateRangeByClientId(
			@PathVariable(value = "startDate") String startDate, @PathVariable(value = "endDate") String endDate,
			@PathVariable(value = "status") Boolean status, @PathVariable(value = "clientId") String clientId,
			Pageable pageable) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "PostSchedule fetched successfully.",
				postService.postScheduleWithRangeStartAndEndByClientId(startDate, endDate, status, clientId, pageable),
				postService.postScheduleWithRangeStartAndEndByClientId(startDate, endDate, status, clientId, null)
						.size()),
				HttpStatus.OK);
	}

	/**
	 * <b>Get PostSchedule by start date,end dateand status</b>
	 * @param startDate
	 *            startDate must be AlphaNumeric and pass as path variable.
	 * @param endDate
	 *            endDate must be AlphaNumeric and pass as path variable.
	 * @param status
	 *            status must be int and pass as path variable.
	 * @param clientId
	 *            clientId must be AlphaNumeric and pass as path variable.
	 * @param pageable
	 * @param query
	 * @return  returns List of PostScheduleResponse Object
	 * <b></br>URL FOR API :</b> /api/admin/post/postSchedule/startDate/{startDate}/endDate/{endDate}/status/{status}/calander
	 */
	@RequestMapping(value = "postSchedule/startDate/{startDate}/endDate/{endDate}/status/{status}/calander", method = RequestMethod.GET)
	public ResponseEntity<Response<List<PostScheduleResponse>>> postScheduleWithDateRangeCalender(
			@PathVariable(value = "startDate") String startDate, @PathVariable(value = "endDate") String endDate,
			@PathVariable(value = "status") Boolean status,

			@RequestParam(value = "clientId", required = false) String clientId, Pageable pageable,
			@RequestParam(value = "q", required = false) String query) {
 
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "PostSchedule fetched successfully.",
				postService.postScheduleWithRangeStartAndEndCalender(startDate, endDate, status, clientId, pageable,
						query)
				),
				HttpStatus.OK);
	}

	
}
	
