package com.socioseer.restapp.web;

import java.util.List;
import java.util.Map;

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
import com.socioseer.common.domain.model.post.Task;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.TaskService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Task.</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "task", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskCotroller {

	@Autowired
	private TaskService taskService;

	/**
	 * <b>Save Task</b>
	 * 
	 * @param task
	 *            Task Details in Json format
	 * @return returns Task Object
	 * <b></br>URL FOR API :</b> /api/admin/task
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Task>> save(@RequestBody Task task) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Task saved successfully.", taskService.save(task)),
				HttpStatus.OK);
	}

	/**
	 * <b>Update Task</b>
	 * 
	 * @param taskId
	 *            taskId must be AlphaNumeric and pass as path variable.
	 * @param task
	 *            task Details in Json format
	 * @return returns the Task Object
	 * <b></br>URL FOR API :</b> /api/admin/task/{taskId}
	 */
	@RequestMapping(value = "{taskId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response<Task>> update(@PathVariable("taskId") String taskId, @RequestBody Task Task) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Task updated successfully.", taskService.update(taskId, Task)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get Task by approverId and status</b>
	 * 
	 * @param approverId
	 *            approverId must be AlphaNumeric and pass as path variable.
	 * @param status
	 *            status must be integer and in {1,2,3}
	 * @return returns the List of Task Object
	 * <b></br>URL FOR API :</b> /api/admin/task/aproverStatus
	 */
	@RequestMapping(value = "aproverStatus", method = RequestMethod.GET)
	public ResponseEntity<Response<List<Task>>> findByApproverAndStatus(@RequestParam("approverId") String approverId,
			@RequestParam("status") int status) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Task fetched successfully.",
				taskService.findByApproverAndStatus(approverId, status)), HttpStatus.OK);
	}

	/**
	 * <b>Get Task by taskId</b>
	 * 
	 * @param taskId
	 *            taskId must be AlphaNumeric and pass as path variable.
	 * @return returns the Task Object if found.
	 * <b></br>URL FOR API :</b> /api/admin/task/{taskId}
	 */
	@RequestMapping(value = "{taskId}", method = RequestMethod.GET)
	public ResponseEntity<Response<Task>> get(@PathVariable("taskId") String taskId) {
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Task fetched successfully.", taskService.get(taskId)),
				HttpStatus.OK);
	}

	/**
	 * <b>Get Tasks by clientId</b>
	 * 
	 * @param clientId
	 *            clientId must be AlphaNumeric and pass as path variable.
	 * @param queryt
	 *            criteria parameters.
	 * @param pageable
	 * @return returns the tasks list
	 * <b></br>URL FOR API :</b> /api/admin/task/client/{clientId}
	 */
	@RequestMapping(value = "client/{clientId}", method = RequestMethod.GET)
	public ResponseEntity<Response<List<Task>>> getTaskByClientId(@PathVariable("clientId") String clientId,
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Task fetched successfully by client id.",
				taskService.getTaskByClientId(clientId, pageable, QueryParser.parse(query)),
				taskService.getTaskByClientId(clientId, null, QueryParser.parse(query)).size()), HttpStatus.OK);
	}

	/**
	 * <b>Get All Task</b>
	 * 
	 * @param query
	 *            query be criteria parameters and pass as path variable.
	 * @param pageable
	 * @return returns Task List.
	 * <b></br>URL FOR API :</b> /api/admin/task/all
	 */
	@RequestMapping(value = "all", method = RequestMethod.GET)
	public ResponseEntity<Response<List<Task>>> getAllClients(@RequestParam(value = "q", required = false) String query,
			Pageable pageable) {
		List<Filter> filters = QueryParser.parse(query);
		return new ResponseEntity<>(
				new Response<>(HttpStatus.OK.value(), "Task fetched successfully",
						taskService.getAllTasks(pageable, filters), taskService.getAllTasks(null, filters).size()),
				HttpStatus.OK);
	}

	/**
	 * <b>Count Task by User Type and Task Type</b>
	 * 
	 * @param usertype
	 *            usertype in {client,user}
	 * @param key
	 *            if user type is client then key is clientId and if usertype is
	 *            user then key is createdBy.
	 * @param query
	 *            query be criteria parameters and pass as path variable.
	 * @param pagable
	 * @return returns Map<String,Integer> where String represents task type and
	 *         Integer returns task count.
	 * <b></br>URL FOR API :</b> /api/admin/task/count/{usertype}/{key}
	 */
	@RequestMapping(value = "count/{usertype}/{key}", method = RequestMethod.GET)
	public ResponseEntity<Response<Map<String, Integer>>> countByUserType(
			@PathVariable(value = "usertype") String usertype, @PathVariable(value = "key") String key,
			@RequestParam(value = "q", required = false) String query, Pageable pageable) {
		List<Filter> filters = QueryParser.parse(query);
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Task fetched successfully",
				taskService.countByUserType(pageable, filters, usertype, key)), HttpStatus.OK);
	}

	/**
	 * <b>Get Tasks by userId</b>
	 * 
	 * @param userId
	 *            userId be criteria parameters and pass as path variable.
	 * @param query
	 *            query be criteria parameters and pass as path variable.
	 * @return returns Task List.
	 * <b></br>URL FOR API :</b> /api/admin/task/get/user/{userId}
	 */
	@RequestMapping(value = "get/user/{userId}", method = RequestMethod.GET)
	public ResponseEntity<Response<List<Task>>> countAllTodayTasks(@PathVariable(value = "userId") String userId,
			@RequestParam(value = "q", required = false) String query) {
		List<Filter> filters = QueryParser.parse(query);
		return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Task fetched successfully",
				taskService.countAllTodayTasks(null, filters, userId),
				taskService.countAllTodayTasks(null, filters, userId).size()), HttpStatus.OK);
	}

}
