package com.socioseer.restapp.service.impl;

import static com.socioseer.common.constants.StatusConstants.CAMPAIGN_POST_CREATED;
import static com.socioseer.common.constants.StatusConstants.CAMPAIGN_POST_DRAFT;
import static com.socioseer.common.constants.StatusConstants.POST_TASK_ACTION_PENDING;
import static com.socioseer.restapp.service.util.DateUtil.getCurrentTimeInMilliseconds;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.NotificationMeaage;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Alert;
import com.socioseer.common.domain.model.Audience;
import com.socioseer.common.domain.model.AudienceType;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.campaign.SocialPlatform;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.domain.model.post.PostScheduleResponse;
import com.socioseer.common.domain.model.post.Task;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.PostDao;
import com.socioseer.restapp.dao.api.SocialHandlerDao;
import com.socioseer.restapp.service.api.AlertService;
import com.socioseer.restapp.service.api.AudienceService;
import com.socioseer.restapp.service.api.AudienceTypeService;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.service.api.MediaService;
import com.socioseer.restapp.service.api.PostCommectService;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.api.PostService;
import com.socioseer.restapp.service.api.SocialPlatformService;
import com.socioseer.restapp.service.api.TaskService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;
import com.socioseer.restapp.service.util.RoleUtil;
import com.socioseer.restapp.util.QueryParser;
import com.socioseer.restapp.util.UrlUtil;

/**
 * <h3>PostService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class PostServiceImpl implements PostService {

	@Autowired
	private PostDao postDao;

	@Autowired
	private SocialHandlerDao socialHandlerDao;

	@Autowired
	private UserService userService;

	@Autowired
	private AudienceService audienceService;

	@Autowired
	private AudienceTypeService audienceTypeService;

	@Autowired
	private PostScheduleService postScheduleService;

	@Autowired
	private CampaignService campaignService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private SocialPlatformService socialPlatformService;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	private AlertService notificationService;

	@Autowired
	private PostCommectService postCommectService;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private UrlUtil urlUtil;

	private static final String RUN_AT = "runAt";
	private static final String IS_EXECUTED = "isExecuted";
	private static final String CAMPAIGN_ID = "campaignId";

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	private static final Long TWO_DAYS = 2 * 86400000L;

	/**
	 * <b>Save Post</b>
	 * @param post
	 * @return	returns Post
	 */
	@Override
	public Post save(@NonNull Post post) {
		validatePost(post);
		List<SocialHandler> socialHandlers = getHandlers(post.getSelectedHandlers());
		if (CollectionUtils.isEmpty(socialHandlers)) {
			throw new IllegalArgumentException("social handlers can not be empty");
		}
		post.setSocialHandlers(socialHandlers);
		post.setStatus(post.getIsDraft() ? CAMPAIGN_POST_DRAFT : CAMPAIGN_POST_CREATED);
		post.setCreatedDate(getCurrentTimeInMilliseconds());
		if (!StringUtils.isEmpty(post.getCreatedBy())) {
			User user = getUserById(post.getCreatedBy());
			if (!ObjectUtils.isEmpty(user)) {
				post.setCreatedName(user.getFullName());
			}
		}

		try {
			return postDao.save(post);
		} catch (Exception e) {
			String message = String.format("Error while saving post for client : %s", post.getClientId());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update Post</b>
	 * @param postId
	 * @param post
	 * @return	returns Post
	 */
	@Override
	public Post update(@NonNull String postId, @NonNull Post post) {
		try {
			validatePost(post);

			Post existingPost = postDao.findOne(postId);
			if (ObjectUtils.isEmpty(existingPost)) {
				String message = String.format("No post found to update with id : %s", postId);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			post.setId(postId);
			if (post.getStatus() == StatusConstants.CAMPAIGN_POST_PENDING) {
				createAlert(post, ModelConstants.NOTIFICATION_TASK, NotificationMeaage.MESSAGE_FOR_PENDING,
						StatusConstants.NOTIFICATION_NOT_VIEWED, NotificationMeaage.DESCRIPTION_FOR_PENDING);
			} else if (post.getStatus() == StatusConstants.CAMPAIGN_POST_APPROVED) {
				createAlert(post, ModelConstants.NOTIFICATION_TASK, NotificationMeaage.MESSAGE_FOR_APPROVED,
						StatusConstants.NOTIFICATION_NOT_VIEWED, NotificationMeaage.DESCRIPTION_FOR_APPROVED);
			} else if (post.getStatus() == StatusConstants.CAMPAIGN_POST_REJECTED) {
				createAlert(post, ModelConstants.NOTIFICATION_TASK, NotificationMeaage.MESSAGE_FOR_REJECTED,
						StatusConstants.NOTIFICATION_NOT_VIEWED, NotificationMeaage.DESCRIPTION_FOR_REJECT);
			}else if(post.getStatus() == StatusConstants.CAMPAIGN_POST_EXPIRED){
				createAlert(post, ModelConstants.NOTIFICATION_TASK, NotificationMeaage.MESSAGE_FOR_EXPIRED,
						StatusConstants.NOTIFICATION_NOT_VIEWED, NotificationMeaage.DESCRIPTION_FOR_EXPIRED);
			}
			if (!StringUtils.isEmpty(post.getCreatedBy())) {
				User user = getUserById(post.getCreatedBy());
				if (!ObjectUtils.isEmpty(user)) {
					post.setCreatedName(user.getFullName());
				}
			}
			return postDao.save(post);
		} catch (Exception e) {
			String message = String.format("Error while updating media with id : %s", postId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update approver in post</b>
	 * @param postId
	 * @param post
	 * @param approvedBy
	 * @return	returns Post
	 */
	@Override
	public Post updateApprovedBy(@NonNull String postId, @NonNull Post post, @NonNull String approvedBy) {

		Post existingPost = postDao.findOne(postId);
		if (ObjectUtils.isEmpty(existingPost)) {
			String message = String.format("No post found to update with id : %s", postId);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		post.setApprovedBy(approvedBy);
		return postDao.save(post);
	}

	/**
	 * <b>Get Post by postId</b>
	 * @param postId
	 * @return	returns Post
	 */
	@Override
	public Post get(@NonNull String postId) {
		try {
			Post post = postDao.findOne(postId);
			getMediaUrl(post);
			setAudiaceNameInPost(post);
			return post;
		} catch (Exception e) {
			String message = String.format("Error while fetching post by id : %s", postId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Active Post</b>
	 * @param postId
	 * @param post
	 * @return	returns Post
	 */
	public Post activatePost(String postId, Post post) {
		Post existingPost = null;
		try {
			existingPost = get(postId);
			if (Objects.isNull(existingPost)) {
				throw new SocioSeerException("Post not found for postId " + post.getId());
			}
			// validateAudience(post.getAudiences());
			String updatedBy = post.getUpdatedBy();
			if (isRoleApprover(updatedBy)) {
				existingPost.setApprovedBy(updatedBy);
				Task task = taskService.getTaskByPostId(existingPost.getId());
				if(post.getStatus()==StatusConstants.CAMPAIGN_POST_APPROVED || existingPost.getStatus()==StatusConstants.CAMPAIGN_POST_CREATED){
					existingPost.setStatus(StatusConstants.CAMPAIGN_POST_APPROVED);
					postScheduleService.setActive(postId, updatedBy);
					if (!ObjectUtils.isEmpty(task)) {
						task.setStatus(StatusConstants.POST_TASK_APPROVED);
						taskService.update(task.getId(), task);
					}
				}else {
					task.setStatus(StatusConstants.POST_TASK_REJECT);
					taskService.update(task.getId(), task);
					existingPost.setStatus(StatusConstants.CAMPAIGN_POST_REJECTED);
					postDao.save(existingPost);
				}
			} else {
				existingPost.setStatus(StatusConstants.CAMPAIGN_POST_PENDING);
				String approverId = findApprover(existingPost.getCampaignId());
				Task task = taskService.save(createNewTask(approverId, existingPost));
				if (!ObjectUtils.isEmpty(task)) {
					existingPost.setTaskId(task.getId());
				}
			}
			existingPost.setAudiences(post.getAudiences());
			existingPost.setUpdatedDate(getCurrentTimeInMilliseconds());
			existingPost.setUpdatedBy(updatedBy);
			createAlert(existingPost, ModelConstants.NOTIFICATION_TASK, NotificationMeaage.MESSAGE_FOR_ACTIVATED,
					StatusConstants.NOTIFICATION_NOT_VIEWED, NotificationMeaage.DESCRIPTION_FOR_ACTIVATED);
			postDao.save(existingPost);
		} catch (Exception e) {
			String message = StringUtils.isEmpty(e.getMessage())
					? String.format("Error while saving audiences for post id : %s", post.getId()) : e.getMessage();
			log.error(message, e);
			throw new SocioSeerException(message);
		}
		return post;
	}

	/**
	 * <b>Create New Task</b>
	 * @param approverId
	 * @param post
	 * @return	returns Task
	 */
	private Task createNewTask(String approverId, Post post) {
		Task task = new Task();
		task.setApproverId(approverId);
		task.setClientId(post.getClientId());
		task.setCampaignId(post.getCampaignId());
		task.setCampaignTitle(post.getCampaignTitle());
		task.setStatus(POST_TASK_ACTION_PENDING);
		task.setPostId(post.getId());
		task.setCreatedBy(post.getCreatedBy());
		task.setCreatedDate(getCurrentTimeInMilliseconds());
		return task;
	}

	/**
	 * <b>Get Approver by campaignId</b>
	 * @param campaignId
	 * @return	returns String
	 */
	private String findApprover(String campaignId) {
		Campaign campaign = campaignService.get(campaignId);
		if (Objects.isNull(campaign)) {
			String message = String.format("No Campaign found for ", campaignId);
			log.error(message);
			throw new SocioSeerException(message);
		}
		return campaign.getTeam().getContentApproversList().get(0);
	}

	/**
	 * <b>Validate Audience</b>
	 * @param audiences
	 */
	private void validateAudience(List<String> audiences) {
		if (CollectionUtils.isEmpty(audiences)) {
			throw new IllegalArgumentException("Audiences are empty");
		}
		Predicate<String> checkAudience = audienceId -> !audienceService.isExists(audienceId);
		boolean isAudienceInvalid = audiences.stream().anyMatch(checkAudience);
		if (isAudienceInvalid) {
			throw new IllegalArgumentException("AudienceIds are invalid " + audiences);
		}
	}

	/**
	 * <b>Get Handlers</b>
	 * @param socialHandlersMap
	 * @return	returns	SocialHandler list
	 */
	private List<SocialHandler> getHandlers(Map<String, List<String>> socialHandlersMap) {
		List<SocialHandler> socialHandlers = new ArrayList<SocialHandler>();
		Optional<SocialHandler> socialHanlder = null;
		SocialPlatform socialPlatform = null;
		boolean isSocialHandler;
		for (Entry<String, List<String>> entry : socialHandlersMap.entrySet()) {
			isSocialHandler = false;
			for (String handlerId : entry.getValue()) {
				socialPlatform = socialPlatformService.get(entry.getKey());
				if (ObjectUtils.isEmpty(socialPlatform)) {
					String message = String.format("Social plateform not null/empty by id %s :", entry.getKey());
					log.info(message);
					throw new IllegalArgumentException(message);
				}
				socialHanlder = socialHandlerDao.findByIdAndSocialPlatform(handlerId, socialPlatform);
				if (socialHanlder.isPresent()) {
					socialHandlers.add(socialHanlder.get());
					isSocialHandler = true;
					continue;
				}
			}
			if (isSocialHandler = false)
				throw new SocioSeerException("Handler not found");
		}
		return socialHandlers;
	}

	/**
	 * <b>Validate Post</b>
	 * @param post
	 */
	private void validatePost(Post post) {
		if (StringUtils.isEmpty(post.getClientId())) {
			log.info("Client id can not be empty/null");
			throw new IllegalArgumentException("Client id can not be empty/null");
		}

		if (post.getSelectedHandlers().isEmpty()) {
			log.info("Handlers can not be empty/null");
			throw new IllegalArgumentException("Handlers can not be empty/null");
		}

		if (StringUtils.isEmpty(post.getCreatedBy())) {
			log.info("Created by can not be empty/null");
			throw new IllegalArgumentException("Created by can not be empty/null");
		}
	}

	/**
	 * <b>Check user role is Approver or not</b>
	 * @param userId
	 * @return	returns boolean
	 */
	private boolean isRoleApprover(String userId) {
		User user = userService.get(userId);
		if (ObjectUtils.isEmpty(user)) {
			throw new SocioSeerException(String.format("User %s not found", userId));
		}
		if (RoleUtil.doUserHaveRole(user.getSecurityGroups(), ModelConstants.ROLE_CONTENT_APPROVER)) {
			return true;
		}
		return false;
	}

	/**
	 * <b>Get All Draft post</b>
	 * @param clientId
	 * @param filters
	 * @param pageable
	 * @return	returns Post List
	 */
	@Override
	public List<Post> getDraftPost(@NonNull String clientId, List<Filter> filters, Pageable pageable) {
		try {
			Query createQuery = QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.CLIENT_ID, clientId),
					pageable);
			List<Post> posts = mongoTemplate.find(createQuery, Post.class);
			return posts;
		} catch (Exception e) {
			String message = String.format("Error while fetching draft post by clientId : %s", clientId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Delete Post by postId and deletedBy</b>
	 * @param id
	 * @param deletedBy
	 */
	@Override
	public void delete(@NonNull String id, @NonNull String deletedBy) {

		try {
			Post post = postDao.findOne(id);
			if (ObjectUtils.isEmpty(post)) {
				String message = String.format("Error while fetching post by post id %s", id);
				log.info(message);
				throw new SocioSeerException(message);
			}
			post.setStatus(StatusConstants.DELETED);
			post.setUpdatedBy(deletedBy);
			post.setUpdatedDate(getCurrentTimeInMilliseconds());
			postDao.save(post);
		} catch (Exception e) {
			String message = String.format("Error while deleting by post id %s", id);
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get All post of Campaign</b>
	 * @param campaignId
	 * @return	returns Post list
	 */
	@Override
	public List<Post> findAllByCampaignId(@NonNull String campaignId) {

		try {
			return postDao.findAllByCampaignId(campaignId);
		} catch (Exception e) {
			String message = String.format("Error while fetching post by compaign id %s", campaignId);
			log.error(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get All Posts</b>
	 * @param pageable
	 * @param filters
	 * @return	returns Post list
	 */
	@Override
	public List<Post> getAllPost(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			List<Post> posts = mongoTemplate.find(query, Post.class);
			getMediaUrls(posts);
			return posts;
		} catch (Exception e) {
			log.error("Error while fetching posts.", e);
			throw new SocioSeerException("Error while fetching posts.");
		}
	}

	/**
	 * <b>Get All posts of Campaigns and Handlers</b>
	 * @param campaignIds
	 * @param socialHandlers
	 * @return	returns Post list
	 */
	@Override
	public List<Post> findAllByCampaignIdAndSocialHandlers(@NonNull Set<String> campaignIds,
			@NonNull List<SocialHandler> socialHandlers) {
		return postDao.findAllByCampaignIdInAndSocialHandlersIn(campaignIds, socialHandlers);
	}

	/**
	 * <b>Create Notification</b>
	 * @param post
	 * @param task
	 * @param meaages
	 * @param status
	 * @param description
	 */
	private void createAlert(Post post, String task, String meaages, int status, String description) {
		try {
			Alert notofication = new Alert();
			notofication.setUpdatedBy(post.getCreatedBy());
			notofication.setUpdatedBy(post.getCreatedBy());
			notofication.setUserId(post.getCreatedBy());
			notofication.setNotificationType(task);
			notofication.setMessage(meaages);
			notofication.setDescription(description);
			notofication.setStatus(status);
			notofication.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			notofication.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			notificationService.save(notofication);
		} catch (Exception e) {
			log.error("Error while saving alert", e);
			throw new SocioSeerException("Error while saving alert.");
		}
	}

	/**
	 * <b>Get PostSchedule list </b>
	 * @param pageable
	 * @param filters
	 * @return	returns PostSchedule list
	 */
	@Override
	public List<PostSchedule> getAllPostSchedules(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			return mongoTemplate.find(query, PostSchedule.class);
		} catch (Exception e) {
			log.error("Error while fetching post schedules .", e);
			throw new SocioSeerException("Error while fetching post schedules");
		}
	}

	/**
	 * <b>Get PostSchedule list within date range and status</b>
	 * @param startDateString
	 * @param status
	 * @param queryString
	 * @param pageable
	 * @return	returns PostSchedule list
	 */
	@Override
	public List<PostSchedule> postScheduleWithRange(String startDateString, Boolean status, String queryString,
			Pageable pageable) {
		Query query = null;
		if (!StringUtils.isEmpty(queryString)) {
			List<Filter> filters = QueryParser.parse(queryString);
			query = QueryBuilder.createQuery(filters, pageable);
		} else {
			query = QueryBuilder.createQuery(null, pageable);
		}

		try {
			Long first = Long.parseLong(startDateString);
			Date startDateFirst = setDateformat(startDateString);
			Long secondDate;
			if (status) {
				secondDate = first - TWO_DAYS;
				Date startDateSecond = setDateformat(secondDate + "");
				query.addCriteria(
						new Criteria().andOperator(Criteria.where("runAt").gte(startDateSecond).lte(startDateFirst),
								Criteria.where("isExecuted").is(status),Criteria.where("isActive").is(true)));
				query.with(new Sort(Sort.Direction.DESC, "runAt"));
			} else {
				secondDate = first + TWO_DAYS;
				Date startDateSecond = setDateformat(secondDate + "");
				query.addCriteria(
						new Criteria().andOperator(Criteria.where("runAt").gte(startDateFirst).lte(startDateSecond),
								Criteria.where("isExecuted").is(status),Criteria.where("isActive").is(true)));
				query.with(new Sort(Sort.Direction.DESC, "runAt"));
			}
			List<PostSchedule> postSchedules = mongoTemplate.find(query, PostSchedule.class);
			if (!CollectionUtils.isEmpty(postSchedules)) {
				removeAccessToken(postSchedules);
			}
			setMdeiaUrls(postSchedules);
			postSchedules.forEach(postSchedule -> {
				Post post = postSchedule.getPost();
				postSchedule.setPost(getMediaUrl(post));
			});

			return postSchedules;
		} catch (Exception e) {
			log.error("Error while fetching post schedules .", e);
			throw new SocioSeerException("Error while fetching post schedules");
		}
	}

	/**
	 * <b>Set Date Format</b>
	 * @param startDateString
	 * @return	returns Date
	 */
	private Date setDateformat(String startDateString) {
		try {
			String dateString = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
					.format(Long.parseUnsignedLong((String) startDateString));
			Date date = DateUtil.getDate(dateString, "dd/MM/yyyy HH:mm");
			return date;
		} catch (Exception e) {
			log.error("Error while parsing date format for postSchedule .", e);
			throw new SocioSeerException("Error while parshing date format for postSchedule ");
		}
	}

	/**
	 * 
	 * @param postSchedules
	 */
	private void removeAccessToken(List<PostSchedule> postSchedules) {
		try {
			postSchedules.forEach(postSchedule -> {
				Post post = postSchedule.getPost();
				if (!ObjectUtils.isEmpty(post)) {
					List<SocialHandler> socialHandlers = post.getSocialHandlers();
					socialHandlers.forEach(socialHandler -> {
						socialHandler.setAccessToken(null);
					});
					post.setSocialHandlers(socialHandlers);
					postSchedule.setPost(post);
				}
			});
		} catch (Exception e) {
			log.error("Error while filtering data from postSchedule", e);
			throw new SocioSeerException("Error while filtering data from postSchedule");
		}
	}

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
	@Override
	public List<PostSchedule> postScheduleWithRangeStartAndEnd(String startDateString, String endDate, Boolean status,
			String clientId, String campaignId, Pageable pageable, String queryString) {
		Query query = null;
		try {
			Date date = setDateformat(endDate);
			date = getEndOfDay(date);
			endDate = String.valueOf(date.getTime());
			query = createQuery(startDateString, endDate, status, clientId, campaignId, pageable, queryString);
			List<PostSchedule> postSchedules = mongoTemplate.find(query, PostSchedule.class);
			getSocialHandlersNames(postSchedules);
			if (!CollectionUtils.isEmpty(postSchedules)) {
				removeAccessToken(postSchedules);
			}
			setMdeiaUrls(postSchedules);
			setAudianceName(postSchedules);
			postSchedules.forEach(postSchedule ->{
				Post post = postSchedule.getPost();
				postSchedule.setPost(getMediaUrl(post));
			});
			return postSchedules;
		} catch (Exception e) {
			log.error("Error while fetching postSchedules .", e);
			throw new SocioSeerException("Error while fetching postSchedules");
		}
	}

	/**
	 * 
	 * @param postSchedules
	 */
	private void setMdeiaUrls(List<PostSchedule> postSchedules) {
		if (!CollectionUtils.isEmpty(postSchedules)) {
			postSchedules.forEach(postSchedule -> {
				Post post = postSchedule.getPost();
				if (!ObjectUtils.isEmpty(post)) {
					User user = userService.get(post.getCreatedBy());
					if (!ObjectUtils.isEmpty(user)) {
						post.setCreatedName(user.getFullName());
					}
					List<Media> medias = new ArrayList<Media>();
					if (!CollectionUtils.isEmpty(post.getMediaIds())) {
						post.getMediaIds().forEach(id -> {
							if (!StringUtils.isEmpty(id)) {
								Media media = mediaService.get(id);
								if (!ObjectUtils.isEmpty(media)) {
									if (!ObjectUtils.isEmpty(media)) {
										media.setUrl(urlUtil.getUrl(
												media.getClientId() + File.separator + media.getMediaType(),
												media.getHashFileName()));
										medias.add(media);
									}
								}
							}
						});
					}
					post.setMediaUrls(medias);
					postSchedule.setPost(post);
				}
			});
		}
	}

	/**
	 * 
	 * @param postSchedules
	 */
	private void setAudianceName(List<PostSchedule> postSchedules) {
		if (!CollectionUtils.isEmpty(postSchedules)) {
			postSchedules.forEach(postSchedule -> {
				Post post = postSchedule.getPost();
				List<String> audiencesName = new ArrayList<String>();
				if(!ObjectUtils.isEmpty(post)){
					if (!CollectionUtils.isEmpty(post.getAudiences())) {
						post.getAudiences().forEach(id -> {
							if (!StringUtils.isEmpty(id)) {
								AudienceType audienceType = audienceTypeService.get(id);
								if (!ObjectUtils.isEmpty(audienceType)) {
									audiencesName.add(audienceType.getName());
								}
							}
						});
					}	
				}
				post.setAudiencesName(audiencesName);
				postSchedule.setPost(post);
			});
		}
	}

	/**
	 * 
	 * @param post
	 */
	private void setAudiaceNameInPost(Post post) {
		List<String> audiencesName = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(post.getAudiences())) {
			post.getAudiences().forEach(id -> {
				if (!StringUtils.isEmpty(id)) {
					AudienceType audienceType = audienceTypeService.get(id);
					if (!ObjectUtils.isEmpty(audienceType)) {
						audiencesName.add(audienceType.getName());
					}
				}
			});
		}
		post.setAudiencesName(audiencesName);
	}

	/**
	 * <b>Get PostSchedule list</b>
	 * @param startDateString
	 * @param status
	 * @param clientId
	 * @param queryString
	 * @param pageable
	 * @return	returns PostSchedule list
	 */
	@Override
	public List<PostSchedule> postScheduleWithRangeByClientId(String startDateString, Boolean status, String clientId,
			String queryString, Pageable pageable) {
		Query query = null;
		if (!StringUtils.isEmpty(queryString)) {
			List<Filter> filters = QueryParser.parse(queryString);
			query = QueryBuilder.createQuery(filters, pageable);
		} else {
			query = QueryBuilder.createQuery(null, pageable);
		}
		try {
			Long first = Long.parseLong(startDateString);
			Date startDateFirst = setDateformat(startDateString);
			Long secondDate;
			if (status) {
				secondDate = first - TWO_DAYS;
				Date startDateSecond = setDateformat(secondDate + "");
				query.addCriteria(
						new Criteria().andOperator(Criteria.where("runAt").gte(startDateSecond).lte(startDateFirst),
								Criteria.where(ModelConstants.CLIENT_ID).is(clientId),
								Criteria.where("isExecuted").is(status)));
				query.with(new Sort(Sort.Direction.DESC, "runAt"));
			} else {
				secondDate = first + TWO_DAYS;
				Date startDateSecond = setDateformat(secondDate + "");
				query.addCriteria(
						new Criteria().andOperator(Criteria.where("runAt").gte(startDateFirst).lte(startDateSecond),
								Criteria.where(ModelConstants.CLIENT_ID).is(clientId),
								Criteria.where("isExecuted").is(status)));
				query.with(new Sort(Sort.Direction.DESC, "runAt"));
			}
			List<PostSchedule> postSchedules = mongoTemplate.find(query, PostSchedule.class);
			if (!CollectionUtils.isEmpty(postSchedules)) {
				removeAccessToken(postSchedules);
			}
			setMdeiaUrls(postSchedules);
			postSchedules.forEach(postSchedule -> {
				Post post = postSchedule.getPost();
				postSchedule.setPost(getMediaUrl(post));
			});
			return postSchedules;
		} catch (Exception e) {
			log.error("Error while fetching postSchedules .", e);
			throw new SocioSeerException("Error while fetching postSchedules");
		}
	}

	/**
	 * <b>Get PostSchedule list</b>
	 * @param startDateString
	 * @param endDate
	 * @param status
	 * @param clientId
	 * @param pageable
	 * @return	returns PostSchedule list
	 */
	@Override
	public List<PostSchedule> postScheduleWithRangeStartAndEndByClientId(String startDateString, String endDate,
			Boolean status, String clientId, Pageable pageable) {
		Query query = QueryBuilder.createQuery(null, pageable);
		try {
			Date startDateFirst = setDateformat(startDateString);
			Date startDateSecond = setDateformat(endDate);
			startDateSecond = getEndOfDay(startDateSecond);
			query.addCriteria(new Criteria().andOperator(
					Criteria.where("runAt").gte(startDateFirst).lte(startDateSecond),
					Criteria.where(ModelConstants.CLIENT_ID).is(clientId), Criteria.where("isExecuted").is(status)));
			query.with(new Sort(Sort.Direction.DESC, "runAt"));
			List<PostSchedule> postSchedules = mongoTemplate.find(query, PostSchedule.class);
			if (!CollectionUtils.isEmpty(postSchedules)) {
				removeAccessToken(postSchedules);
			}
			setMdeiaUrls(postSchedules);
			postSchedules.forEach(postSchedule -> {
				Post post = postSchedule.getPost();
				postSchedule.setPost(getMediaUrl(post));
			});
			return postSchedules;
		} catch (Exception e) {
			log.error("Error while fetching postSchedules .", e);
			throw new SocioSeerException("Error while fetching postSchedules");
		}
	}
	/**
	 * 
	 * @param date
	 * @return	returns Date
	 */
	private static Date getEndOfDay(Date date) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}
	
	/**
	 * <b>Create Query</b>
	 * @param startDateString
	 * @param endDate
	 * @param status
	 * @param clientId
	 * @param campaignId
	 * @param pageable
	 * @param StringQuery
	 * @return	returns Query
	 */
	private Query createQuery(String startDateString, String endDate, Boolean status, String clientId,
			String campaignId, Pageable pageable, String StringQuery) {

		Query query = null;
		if (!StringUtils.isEmpty(StringQuery)) {
			List<Filter> filters = QueryParser.parse(StringQuery);
			query = QueryBuilder.createQuery(filters, pageable);
		} else {
			query = QueryBuilder.createQuery(null, pageable);
		}

		try {
			Date startDateFirst = setDateformat(startDateString);
			Date startDateSecond = setDateformat(endDate + "");
			if (!StringUtils.isEmpty(clientId) && !StringUtils.isEmpty(campaignId)) {
				query.addCriteria(
						new Criteria().andOperator(Criteria.where(RUN_AT).gte(startDateFirst).lte(startDateSecond),
								Criteria.where(IS_EXECUTED).is(status), Criteria.where(CAMPAIGN_ID).is(campaignId),
								Criteria.where(ModelConstants.CLIENT_ID).is(clientId)));
			} else if (!StringUtils.isEmpty(clientId)) {
				query.addCriteria(new Criteria().andOperator(
						Criteria.where(RUN_AT).gte(startDateFirst).lte(startDateSecond),
						Criteria.where(IS_EXECUTED).is(status), Criteria.where(ModelConstants.CLIENT_ID).is(clientId)));
			} else {
				query.addCriteria(
						new Criteria().andOperator(Criteria.where(RUN_AT).gte(startDateFirst).lte(startDateSecond),
								Criteria.where(IS_EXECUTED).is(status)));
			}
			query.with(new Sort(Sort.Direction.DESC, RUN_AT));
			return query;
		} catch (Exception e) {
			log.error("Error while create query .", e);
			throw new SocioSeerException("Error while create query");
		}

	}

	/**
	 * 
	 * @param posts
	 */
	private void getMediaUrls(List<Post> posts) {

		if (!CollectionUtils.isEmpty(posts)) {
			posts.forEach(post -> {
				if (!ObjectUtils.isEmpty(post)) {
					List<Media> medis = new ArrayList<>();
					if (!CollectionUtils.isEmpty(post.getMediaIds())) {
						post.getMediaIds().forEach(id -> {
							if (!StringUtils.isEmpty(id)) {
								Media media = mediaService.get(id);
								if (!ObjectUtils.isEmpty(media)) {
									String url =urlUtil.getUrl(media.getClientId() + File.separator + media.getMediaType(),
											media.getHashFileName());
									media.setUrl(url);
									medis.add(media);
								}
							}
						});
					}
					post.setMediaUrls(medis);
				}	
			});
		}
	}

	/**
	 * 
	 * @param post
	 * @return	returns Post
	 */
	private Post getMediaUrl(Post post) {
		
		if (!ObjectUtils.isEmpty(post)) {
			boolean isImage =false;
			List<String> medisUrls = new ArrayList<>();
			if (!CollectionUtils.isEmpty(post.getMediaIds())) {
				for(String id :post.getMediaIds()){
					if (!StringUtils.isEmpty(id)) {
						Media media = mediaService.get(id);
						if (!ObjectUtils.isEmpty(media)) {
							medisUrls.add(urlUtil.getUrl(media.getClientId() + File.separator + media.getMediaType(),
									media.getHashFileName()));
							isImage = true;
						}
					}

				}
			}
			if(!isImage){
				medisUrls.add(SocioSeerConstant.DEFAULT);
			}
			post.setUrls(medisUrls);
		}
		return post;
	}

	/**
	 * 
	 * @param postId
	 * @param post
	 */
	@Override
	public void updatePostScheduler(String postId, Post post) {
		try {
			post.setId(postId);
			postDao.save(post);
		} catch (Exception e) {
			String message = String.format("Error updating  post by post id %s", postId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

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
	@Override
	public List<PostScheduleResponse> postScheduleWithRangeStartAndEndCalender(String startDateString, String endDate,
			Boolean status, String clientId, Pageable pageable, String queryString) {
		Query query = null;
		try {
			query = createQuery(startDateString, endDate, status, clientId, null, pageable, queryString);
			List<PostSchedule> postSchedules = mongoTemplate.find(query, PostSchedule.class);
			List<String> campaignIds = new ArrayList<String>();
			List<PostScheduleResponse> postScheduleResponses = new ArrayList<PostScheduleResponse>();
			if (!CollectionUtils.isEmpty(postSchedules)) {
				postSchedules.forEach(postSchedule -> {
					boolean state = false;
					Map<Date, Integer> map = new HashMap<Date, Integer>();
					if (!campaignIds.contains(postSchedule.getCampaignId())) {
						for (PostSchedule postScheduleCount : postSchedules) {
							if (!campaignIds.contains(postSchedule.getCampaignId())
									&& postScheduleCount.getCampaignId().equals(postSchedule.getCampaignId())) {
								Date dt = null;
								dt = postScheduleCount.getRunAt();
								try {
									dt = sdf.parse(sdf.format(dt));
								} catch (Exception e) {
									String message = "Error while convert date format";
									log.error(message);
									throw new SocioSeerException(message);
								}
								if (map.get(dt) != null) {
									int count = map.get(dt);
									map.put(dt, ++count);
								} else {
									map.put(dt, 1);
								}
								state = true;
							}
						}
						if (state) {
							map.forEach((k, v) -> {
								PostScheduleResponse postScheduleResponse = new PostScheduleResponse();
								postScheduleResponse.setCampaignId(postSchedule.getCampaignId());
								postScheduleResponse.setCampaignName(postSchedule.getCampaignName());
								postScheduleResponse.setCreateDate(k);
								postScheduleResponse.setCountPost(map.get(k));
								postScheduleResponses.add(postScheduleResponse);
							});
						}
						campaignIds.add(postSchedule.getCampaignId());
					}
				});
			}
			return postScheduleResponses;
		} catch (Exception e) {
			log.error("Error while fetching postSchedules .", e);
			throw new SocioSeerException("Error while fetching postSchedules");
		}
	}
	/**
	 * <b>Get User by userId</b>
	 * @param userId
	 * @return	returns User
	 */
	private User getUserById(String userId) {
		return userService.get(userId);
	}

	/**
	 * <b>Get gSocial Handlers Names by postSchedules</b>
	 * @param postSchedules
	 */
	private void getSocialHandlersNames(List<PostSchedule> postSchedules) {
		Map<String, String> handlesName = new HashMap<>();
		for(PostSchedule postSchedule: postSchedules) {
			Post post = postSchedule.getPost();
			if(!ObjectUtils.isEmpty(post)){
				Map<String, List<String>> selectedHandlers = post.getSelectedHandlers();
				if (!CollectionUtils.isEmpty(selectedHandlers)) {
					for (String handlerKey : selectedHandlers.keySet()) {
						List<String> handlersIds = selectedHandlers.get(handlerKey);
						if (!CollectionUtils.isEmpty(handlersIds)) {
							for(String hand:handlersIds){	
							SocialHandler socialHandler = socialHandlerDao.findOne(hand);
								Map<String, String> map = socialHandler.getAccessToken();
								if (!ObjectUtils.isEmpty(map)) {
									handlesName.put(map.get("fullName"), map.get("screen_name"));
								}
						}
						}
					}
				}
				post.setSocialHandlerName(handlesName);
				handlesName = new HashMap<String,String>();
				postSchedule.setPost(post);
				
			}
			
		}
	}
}
