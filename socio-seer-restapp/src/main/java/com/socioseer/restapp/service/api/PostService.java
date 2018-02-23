package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.domain.model.post.PostScheduleResponse;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Post Services .</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PostService extends CrudApi<Post> {

	/**
	 * <b>Active Post</b>
	 * @param postId
	 * @param post
	 * @return	returns Post
	 */
	public Post activatePost(String postId, Post post);

	/**
	 * <b>Update approver in post</b>
	 * @param postId
	 * @param post
	 * @param approvedBy
	 * @return	returns Post
	 */
	Post updateApprovedBy(String postId, Post post, String approvedBy);

	/**
	 * <b>Get All Draft post</b>
	 * @param clientId
	 * @param filters
	 * @param pageable
	 * @return	returns Post List
	 */
	List<Post> getDraftPost(String clientId, List<Filter> filters, Pageable pageable);

	/**
	 * <b>Delete Post by postId and deletedBy</b>
	 * @param id
	 * @param deletedBy
	 */
	void delete(String id, String deletedBy);

	/**
	 * <b>Get All post of Campaign</b>
	 * @param campaignId
	 * @return	returns Post list
	 */
	List<Post> findAllByCampaignId(String campaignId);

	/**
	 * <b>Get All Posts</b>
	 * @param pageable
	 * @param filters
	 * @return	returns Post list
	 */
	List<Post> getAllPost(Pageable pageable, List<Filter> filters);

	/**
	 * <b>Get All posts of Campaigns and Handlers</b>
	 * @param campaignIds
	 * @param socialHandlers
	 * @return	returns Post list
	 */
	List<Post> findAllByCampaignIdAndSocialHandlers(Set<String> campaignId, List<SocialHandler> socialHandlers);

	/**
	 * <b>Get PostSchedule list </b>
	 * @param pageable
	 * @param filters
	 * @return	returns PostSchedule list
	 */
	List<PostSchedule> getAllPostSchedules(Pageable pageable, List<Filter> filters);

	/**
	 * <b>Get PostSchedule list within date range and status</b>
	 * @param startDateString
	 * @param status
	 * @param queryString
	 * @param pageable
	 * @return	returns PostSchedule list
	 */
	List<PostSchedule> postScheduleWithRange(String startDateString, Boolean status, String queryString,
			Pageable pageable);

	/**
	 * <b>Get PostSchedule list</b>
	 * @param startDateString
	 * @param status
	 * @param clientId
	 * @param queryString
	 * @param pageable
	 * @return	returns PostSchedule list
	 */
	List<PostSchedule> postScheduleWithRangeByClientId(String startDateString, Boolean status, String clientId,
			String query ,Pageable pageable);

	/**
	 * <b>Get PostSchedule list</b>
	 * @param startDateString
	 * @param endDate
	 * @param status
	 * @param clientId
	 * @param pageable
	 * @return	returns PostSchedule list
	 */
	List<PostSchedule> postScheduleWithRangeStartAndEndByClientId(String startDateString, String endDate,
			Boolean status, String clientId, Pageable pageable);

	/**
	 * <b>Get PostSchedule list</b>
	 * @param startDateString
	 * @param endDate
	 * @param status
	 * @param clientId
	 * @param campaignId
	 * @param pageable
	 * @param queryString
	 * @return	returns PostSchedule list
	 */
	List<PostSchedule> postScheduleWithRangeStartAndEnd(String startDateString, String endDate, Boolean status,
			String clientId, String campaignId, Pageable pageable, String queryString);

	/**
	 * 
	 * @param postId
	 * @param post
	 */
	void updatePostScheduler(String postId, Post post);

	/**
	 * <b>Get PostScheduleResponse list</b>
	 * @param startDateString
	 * @param endDate
	 * @param status
	 * @param clientId
	 * @param pageable
	 * @param queryString
	 * @return	returns PostScheduleResponse list
	 */
	List<PostScheduleResponse> postScheduleWithRangeStartAndEndCalender(String startDateString, String endDate, Boolean status,
			String clientId, Pageable pageable, String queryString);

}
