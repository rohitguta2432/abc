package com.socioseer.restapp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.domain.Team;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.post.PostComment;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.PostCommentDao;
import com.socioseer.restapp.service.api.PostCommectService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * <h3>PostCommectService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class PostCommectServiceImpl implements PostCommectService {

	@Autowired
	private PostCommentDao postCommentDao;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserService userService;

	/**
	 * <b>Save PostComment</b>
	 * @param entity
	 * @return	returns PostComment
	 */
	@Override
	public PostComment save(PostComment entity) {
		try {
			postCommentValidation(entity);
			entity.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			entity.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			return postCommentDao.save(entity);
		} catch (Exception e) {
			String message = "Error while saving post comments";
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update PostComment</b>
	 * @param id
	 * @param entity
	 * @return	returns PostComment
	 */
	@Override
	public PostComment update(@NonNull String id, PostComment entity) {

		try {
			postCommentValidation(entity);
			PostComment postCommectExisted = postCommentDao.findOne(id);
			if (ObjectUtils.isEmpty(postCommectExisted)) {
				String message = String.format("Post comment not found by id %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			entity.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			entity.setCreatedDate(postCommectExisted.getCreatedDate());
			entity.setId(postCommectExisted.getId());
			return postCommentDao.save(entity);
		} catch (Exception e) {
			String message = "Error while updating post comments";
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get PostComment by id</b>
	 * @param id
	 * @return returns PostComment
	 */
	@Override
	public PostComment get(@NonNull String id) {

		try {
			return postCommentDao.findOne(id);
		} catch (Exception e) {
			String message = String.format("Error while getting post comment by id %s", id);
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get PostComment list by userId</b>
	 * @param usreId
	 * @return	returns PostComment list
	 */
	@Override
	public List<PostComment> getByUserId(@NonNull String usreId) {

		try {
			return postCommentDao.getByUserId(usreId);
		} catch (Exception e) {
			String message = String.format("Error while getting post comment by user id %s", usreId);
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get PostComment list by postId</b>
	 * @param postId
	 * @return	returns PostComment list
	 */
	@Override
	public List<PostComment> getByPostId(@NonNull String postId) {

		try {
			Query query = new Query(Criteria.where(ModelConstants.POST_ID).is(postId));
			query.with(new Sort(Sort.Direction.DESC, ModelConstants.USER_ID));
			return mongoTemplate.find(query, PostComment.class);
		} catch (Exception e) {
			String message = String.format("Error while getting post comment by post id %s", postId);
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * 
	 * @param entity
	 */
	private void postCommentValidation(PostComment entity) {

		if (StringUtils.isEmpty(entity.getPostId())) {
			log.info("Post id can not  be empty/null");
			throw new IllegalArgumentException("Post id can not  be empty/null");
		}

		if (StringUtils.isEmpty(entity.getCreatedBy())) {
			log.info("Created by can not  be empty/null");
			throw new IllegalArgumentException("Created by can not  be empty/null");
		}

		if (StringUtils.isEmpty(entity.getCreatedByName())) {
			log.info("Created by name can not  be empty/null");
			throw new IllegalArgumentException("Created by name can not  be empty/null");
		}

		if (StringUtils.isEmpty(entity.getUserId())) {
			log.info("User id can not  be empty/null");
			throw new IllegalArgumentException("User id can not  be empty/null");
		}

		User user = userService.get(entity.getUserId());
		if (ObjectUtils.isEmpty(user)) {
			String message = String.format("User not found by user id %s", entity.getUserId());
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

}
