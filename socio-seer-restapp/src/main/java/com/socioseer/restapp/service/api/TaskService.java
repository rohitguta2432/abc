package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.Task;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Task Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface TaskService extends CrudApi<Task> {

	/**
	 * <b>Get Task by ApproverId and Status</b>
	 * 
	 * @param approverId
	 *            approverId as String
	 * @param status
	 *            status as int
	 * 
	 * @return returns List of Task.
	 */
	List<Task> findByApproverAndStatus(String approverId, int status);

	/**
	 * <b>Get Tasks by Client Id</b>
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param pageable
	 * @param filters
	 * 
	 * @return returns List of Tasks.
	 */
	List<Task> getTaskByClientId(String clientId, Pageable pageable, List<Filter> filters);

	/**
	 * <b>Get All Tasks</b>
	 * 
	 * @param pageable
	 * @param filters
	 * 
	 * @return returns List of Tasks.
	 */
	List<Task> getAllTasks(Pageable pageable, List<Filter> filters);

	/**
	 * <b>Count Task By Type</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @param type
	 *            type as String
	 * @param key
	 *            key as String
	 * 
	 * @return returns Map<String,Integer> where key represents TaskType and
	 *         value represents count.
	 */
	Map<String, Integer> countByUserType(Pageable pageable, List<Filter> filters, String type, String key);

	/**
	 * <b>Get Task By PostId</b>
	 * 
	 * @param postId
	 * 
	 * @return returns Task Object
	 */
	Task getTaskByPostId(String postId);

	/**
	 * <b>Get Tasks by UserId</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @param userId
	 *            userId as String
	 * @return returns List of Tasks
	 */
	List<Task> countAllTodayTasks(Pageable pageable, List<Filter> filters, String userId);

	/**
	 * <b>Delete Task by postId</b>
	 * 
	 * @param postId
	 * 
	 */
	void deleteByPostId(String postId);
}
