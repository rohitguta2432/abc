package com.socioseer.restapp.service.api;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.domain.model.request.PostScheduleRequest;

/**
 * <h3>PostSchedule Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PostScheduleService {

  /**
   * <b>Save</b>
   *
   * @param postId the post id
   * @param postScheduleRequest the post schedule request
   * @return the list
   */
  List<PostSchedule> save(String postId, List<PostScheduleRequest> postScheduleRequests);

  /**
   * <b>Sets active</b>
   *
   * @param postId the post id
   * @param updatedBy the updated by
   */
  void setActive(String postId, String updatedBy);

  /**
   * <b>Find by post id</b>
   *
   * @param postId the post id
   * @return the optional
   */
  Optional<List<PostSchedule>> findByPostId(String postId);

  /**
   * <b>Update PostSchedules</b>
   *
   * @param idsToUpdate the ids to update
   */
  void upldateMulti(List<PostSchedule> idsToUpdate);

  /**
   * <b>Find by run at between and is active</b>
   *
   * @param startDate the start date
   * @param endDate the end date
   * @param isActive the is active
   * @return the optional
   */
  Optional<List<PostSchedule>> findByRunAtBetweenAndIsActive(Date startDate, Date endDate,
      boolean isActive);

  /**
   * <b>Find by run at between and is executed</b>
   *
   * @param startDate the start date
   * @param endDate the end date
   * @param isExecuted the is executed
   * @param isActive the is active
   * @return the optional
   */
  Optional<List<PostSchedule>> findByRunAtBetweenAndIsExecutedAndIsActive(Date startDate,
      Date endDate, boolean isExecuted, boolean isActive);

  /**
   * <b>Update social post id</b>
   *
   * @param postScheduleId the post schedule id
   * @param handlerId the handler id
   * @param socialPostId the social post id
   */
  void updateSocialPostId(String postScheduleId, String handlerId, String socialPostId, int statusCode, String message);

  /**
   * <b>Find published post</b>
   *
   * @param postId the post id
   * @return
   */
  List<PostSchedule> findPublishedPost(String postId);


  /**
   * <b>Get PostSchedule by socialPostId</b>
   * @param socialPostId
   * @return	returns PostSchedule
   */
  PostSchedule getPostScheduleBySocialPostId(String socialPostId);

  

}
