package com.socioseer.restapp.dao.api;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.Task;

/**
 * <h3>Task Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface TaskDao extends MongoRepository<Task, String> {
	/**
	 * 
	 * @param approverId
	 *            approverId as String
	 * @param status
	 *            status as int
	 * @return returns List of Tasks
	 */
	List<Task> findAllByApproverIdAndStatus(String approverId, int status);

  
	/**
	 * 
	 * @param postId
	 *            postId as String
	 * 
	 */
	void deleteByPostId(String postId);

	/**
	 * 
	 * @param approverId
	 *            approverId as String
	 * @return returns List of Tasks
	 */
	List<Task> findByApproverId(String approverId);

	/**
	 * 
	 * @param postId
	 *            postId as String
	 * @return returns Task Object
	 */
	Task getTaskByPostId(String postId);

}
