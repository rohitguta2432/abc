package com.socioseer.restapp.dao.api;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.post.PostComment;

/**
 * <h3>PostComment Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PostCommentDao extends PagingAndSortingRepository<PostComment, String> {
	
	/**
	 * 
	 * @param userId
	 * @return		returns PostComment list
	 */
	List<PostComment> getByUserId(String userId);
}
