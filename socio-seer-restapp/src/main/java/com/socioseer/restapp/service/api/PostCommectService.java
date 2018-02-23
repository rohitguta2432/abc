package com.socioseer.restapp.service.api;

import java.util.List;

import com.socioseer.common.domain.model.post.PostComment;

/**
 * <h3>PostComment Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PostCommectService extends CrudApi<PostComment> {

	/**
	 * <b>Get PostComment list by userId</b>
	 * @param usreId
	 * @return	returns PostComment list
	 */
	List<PostComment> getByUserId(String usreId);

	/**
	 * <b>Get PostComment list by postId</b>
	 * @param postId
	 * @return	returns PostComment list
	 */
	List<PostComment> getByPostId(String postId);

}
