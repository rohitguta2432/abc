package com.socioseer.restapp.service.impl;

import static com.socioseer.restapp.service.util.DateUtil.getCurrentTimeInMilliseconds;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.ImmutableMap;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Client;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.domain.model.post.Task;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.TaskDao;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.api.MediaService;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.api.PostService;
import com.socioseer.restapp.service.api.TaskService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.QueryBuilder;
import com.socioseer.restapp.service.util.RoleUtil;
import com.socioseer.restapp.util.UrlUtil;

/**
 * <h3>Task Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

	private Predicate<Integer> isStatusValid = status -> (status == StatusConstants.POST_TASK_APPROVED
			|| status == StatusConstants.POST_TASK_REJECT || status == StatusConstants.POST_TASK_ACTION_PENDING);

	@Autowired
	private TaskDao taskDao;

	@Autowired
	private PostScheduleService postScheduleService;

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private CampaignService campaignService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private MediaService mediaService;

	@Autowired
	private UrlUtil urlUtil;

	final static String ALL = "all";
	final static String SCHEDULED_TASK = "scheduled";

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * <b>Save Task</b>
	 * 
	 * @param task
	 * @return returns Task
	 */
	@Override
	public Task save(Task task) {
		try {
			validateTask(task);
			long createdDate = getCurrentTimeInMilliseconds();
			task.setCreatedDate(createdDate);
			task.setCreatedBy(task.getApproverId());
			task.setStatus(StatusConstants.POST_TASK_ACTION_PENDING);
			return taskDao.save(task);
		} catch (Exception e) {
			String message = String.format("Error while saving task for post id : %s", task.getPostId());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update Task</b>
	 * 
	 * @param id
	 * @param task
	 * @return returns Task
	 */
	@Override
	public Task update(String id, Task task) {
		try {
			validateTask(task);
			Task existingTask = get(id);
			if (ObjectUtils.isEmpty(existingTask)) {
				String message = String.format("No task found to update with task id : %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			int status = task.getStatus();
			if (!isStatusValid.test(status)) {
				String message = String.format("Invalid status with task id : %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			;

			if (task.getStatus() == StatusConstants.POST_TASK_APPROVED) {
				if (isTaskValid(existingTask.getPostId())) {
					updatePostDetails(task);
				} else {
					throw new SocioSeerException(String.format("Task is invalid : %s", task.getId()));
				}
			}
			existingTask.setStatus(status);
			existingTask.setApproverId(task.getApproverId());
			existingTask.setUpdatedBy(task.getUpdatedBy());
			existingTask.setUpdatedDate(getCurrentTimeInMilliseconds());
			return taskDao.save(existingTask);
		} catch (Exception e) {
			String message = StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage()
					: String.format("Error while saving task for post id : %s", task.getPostId());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Check Validity of Task</b>
	 * 
	 * @param postId
	 * @return returns boolean
	 */
	private boolean isTaskValid(String postId) {
		Optional<List<PostSchedule>> postScheduleList = postScheduleService.findByPostId(postId);
		if (!postScheduleList.isPresent()) {
			return false;
		}
		return !postScheduleList.get().stream()
				.anyMatch(postSchedule -> (postSchedule.getRunAt().compareTo(new Date()) <= 0));
	}

	/**
	 * <b>Update Post Detail</b>
	 * 
	 * @param task
	 */
	private void updatePostDetails(Task task) {
		String postId = task.getPostId();
		Post post = postService.get(postId);
		if (Objects.isNull(post)) {
			String message = String.format("Post not found for post id : %s", task.getPostId());
			log.error(message);
			throw new SocioSeerException(message);
		}
		postScheduleService.setActive(postId, task.getApproverId());
		postService.updateApprovedBy(postId, post, task.getApproverId());
		task.setStatus(StatusConstants.POST_TASK_APPROVED);
	}

	/**
	 * <b>Get Task by Approver and status</b>
	 * 
	 * @param approverId
	 * @param status
	 * @return returns List of Task
	 */
	@Override
	public List<Task> findByApproverAndStatus(@NonNull String approverId, int status) {
		try {
			if (status == -1) {
				return taskDao.findByApproverId(approverId);
			}
			List<Task> tasks = isStatusValid.test(status) ? taskDao.findAllByApproverIdAndStatus(approverId, status)
					: Collections.emptyList();

			if (!CollectionUtils.isEmpty(tasks)) {
				getPostByPostId(tasks);
			}
			return tasks;

		} catch (Exception ex) {
			String message = String.format("Error while saving task for approver id : %s", approverId);
			log.error(message, ex);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get Task by taskId</b>
	 * 
	 * @param id
	 * @return returns Task
	 */
	@Override
	public Task get(@NonNull String id) {
		try {
			Task task = taskDao.findOne(id);
			List<String> mediaIds = new ArrayList<String>();
			Post post = null;
			if (!ObjectUtils.isEmpty(task)) {
				Campaign campaign = campaignService.get(task.getPostId());
				if (!ObjectUtils.isEmpty(campaign)) {
				}
				post = postService.get(task.getPostId());
				if (!ObjectUtils.isEmpty(post)) {
					task.setPost(post);
					if (!CollectionUtils.isEmpty(post.getMediaIds())) {
						mediaIds = post.getMediaIds();
						getMediaDetails(task, mediaIds);
					}
				}
			}

			Optional<List<PostSchedule>> postSchedules = postScheduleService.findByPostId(task.getPostId());
			if (postSchedules.isPresent()) {
				PostSchedule postSchedule = postSchedules.get().get(0);
				Date dt = null;
				dt = postSchedule.getRunAt();
				try {
					Date date = new Date();
					if (date.compareTo(dt) > 0) {
						post.setStatus(StatusConstants.CAMPAIGN_POST_EXPIRED);
						postService.update(post.getId(), post);
						task.setStatus(StatusConstants.POST_TASK_EXPIRED);
						taskDao.save(task);
						task.setPost(post);
					}
				} catch (Exception e) {
					String message = "Error while convert date format";
					log.error(message);
					throw new SocioSeerException(message);
				}
			}
			return task;
		} catch (Exception e) {
			String message = String.format("Error while saving task for post id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate Task</b>
	 * 
	 * @param task
	 */
	private void validateTask(Task task) {

		if (StringUtils.isEmpty(task.getPostId())) {
			log.info("post id can not be null/empty.");
			throw new IllegalArgumentException("post id can not be null/empty.");
		}

		if (!StringUtils.isEmpty(task.getApproverId())) {
			if (!isRoleApprover(task.getApproverId())) {
				String message = "approver id " + task.getApproverId() + " is not approver";
				log.info(message);
				throw new IllegalArgumentException(message);
			}
		}

		if (StringUtils.isEmpty(task.getCreatedBy())) {
			log.info("Created by user id can not be null/empty.");
			throw new IllegalArgumentException("Created by user id can not be null/empty.");
		}

		if (StringUtils.isEmpty(task.getClientId())) {
			log.info("Client id can not be null/empty.");
			throw new IllegalArgumentException("Client id can not be null/empty.");
		}

		Client client = clientService.get(task.getClientId());
		if (ObjectUtils.isEmpty(client)) {
			String message = String.format("Client not existed by client Id %s", task.getClientId());
			log.info(message);
			throw new IllegalArgumentException(message);
		}

	}

	/**
	 * <b>Check Approver Role of User by userId</b>
	 * 
	 * @param userId
	 * @return returns boolean
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
	 * <b>Get Task by clientId</b>
	 * 
	 * @param clientId
	 * @param pageable
	 * @param filters
	 * @return returns List of Team
	 */
	@Override
	public List<Task> getTaskByClientId(@NonNull String clientId, Pageable pageable, List<Filter> filters) {
		try {
			List<Task> tasks = mongoTemplate.find(
					QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.CLIENT_ID, clientId), pageable),
					Task.class);
			if (!CollectionUtils.isEmpty(tasks)) {
				getPostByPostId(tasks);
			}
			return tasks;
		} catch (Exception e) {
			String message = String.format("Error while fetching tasks by client id : %s", clientId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get All Task</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @return returns List of Tasks
	 */
	@Override
	public List<Task> getAllTasks(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			List<Task> tasks = mongoTemplate.find(query, Task.class);
			if (!CollectionUtils.isEmpty(tasks)) {
				getPostByPostId(tasks);
			}
			
			return tasks;
		} catch (Exception e) {
			log.error("Error while fetching tasks.", e);
			throw new SocioSeerException("Error while fetching tasks.");
		}
	}

	/**
	 * <b>Get Post by postId from task</b>
	 * 
	 * @param taks
	 */
	private void getPostByPostId(List<Task> taks) {

		try {
			taks.forEach(task -> {
				Optional<List<PostSchedule>> postSchedules = postScheduleService.findByPostId(task.getPostId());
				if (postSchedules.isPresent()) {
					PostSchedule postSchedule = postSchedules.get().get(0);
					Date dt = null;
					dt = postSchedule.getRunAt();
					try {
						Date currentDate = new Date();
						currentDate = sdf.parse(sdf.format(currentDate));
						dt = sdf.parse(sdf.format(dt));
						if (dt.compareTo(currentDate) > 0) {
							Date dateBefore = new Date(dt.getTime() - 24 * 3600 * 1000l);
							postSchedule.setRunAt(dateBefore);
						} else {
							postSchedule.setRunAt(dt);
						}
					} catch (Exception e) {
						String message = "Error while convert date format";
						log.error(message);
						throw new SocioSeerException(message);
					}
					task.setPostSchedule(postSchedule);
					createdByName(task);
				}
				
				updateTaskStatus(task);
			});
		} catch (Exception e) {
			String message = String.format("Error while fetching post");
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}
	
	private void updateTaskStatus(Task task){
		List<String> mediaIds = new ArrayList<String>();
		Post post = null;
		if (!ObjectUtils.isEmpty(task)) {
			Campaign campaign = campaignService.get(task.getPostId());
			if (!ObjectUtils.isEmpty(campaign)) {
			}
			post = postService.get(task.getPostId());
			if (!ObjectUtils.isEmpty(post)) {
				task.setPost(post);
				if (!CollectionUtils.isEmpty(post.getMediaIds())) {
					mediaIds = post.getMediaIds();
					getMediaDetails(task, mediaIds);
				}
			}
		}

		Optional<List<PostSchedule>> postSchedules = postScheduleService.findByPostId(task.getPostId());
		if (postSchedules.isPresent()) {
			PostSchedule postSchedule = postSchedules.get().get(0);
			Date dt = null;
			dt = postSchedule.getRunAt();
			try {
				Date date = new Date();
				if (date.compareTo(dt) > 0) {
					post.setStatus(StatusConstants.CAMPAIGN_POST_EXPIRED);
					postService.update(post.getId(), post);
					task.setStatus(StatusConstants.POST_TASK_EXPIRED);
					taskDao.save(task);
					task.setPost(post);
				}
			} catch (Exception e) {
				String message = "Error while convert date format";
				log.error(message);
				throw new SocioSeerException(message);
			}
		}
	}

	/**
	 * <b>Count Task by User Type</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @param type
	 * @return returns Map<String, Integer>
	 */
	@Override
	public Map<String, Integer> countByUserType(Pageable pageable, List<Filter> filters, String type, String key) {
		try {
			List<Task> tasks = new ArrayList<Task>();
			if (type.equals(SocioSeerConstant.USER_TYPE)) {
				tasks = mongoTemplate.find(
						QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.USER_APPROVER, key), pageable),
						Task.class);
			} else if (type.equals(SocioSeerConstant.CLIENT_TYPE)) {
				tasks = mongoTemplate.find(
						QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.CLIENT_ID, key), pageable),
						Task.class);
			} else {
				Query query = QueryBuilder.createQuery(filters, pageable);
				tasks = mongoTemplate.find(query, Task.class);
			}
			Map<String, Integer> countTasks = new HashMap<String, Integer>();
			if (!CollectionUtils.isEmpty(tasks)) {
				countTasks = countTask(tasks);
			}
			return countTasks;
		} catch (Exception e) {
			String message = String.format("Error while fetching task for count by " + type + " and id" + key);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Count Task</b>
	 * 
	 * @param tasks
	 * @return returns Map<String, Integer>
	 */
	private Map<String, Integer> countTask(List<Task> tasks) {

		Map<String, Integer> countTasks = new HashMap<String, Integer>();
		int pendingTaskCount = 0;
		int approvedTaskCount = 0;
		int rejectedTaskCount = 0;
		for(Task task :tasks){
			if (task.getStatus() == StatusConstants.POST_TASK_ACTION_PENDING) {
				pendingTaskCount++;
			} else if (task.getStatus() == StatusConstants.POST_TASK_APPROVED) {
				approvedTaskCount++;
			} else if (task.getStatus() == StatusConstants.POST_TASK_REJECT) {
				rejectedTaskCount++;
			}
		}
		countTasks.put(SocioSeerConstant.POST_TASK_ACTION_PENDING, pendingTaskCount);
		countTasks.put(SocioSeerConstant.POST_TASK_APPROVED, approvedTaskCount);
		countTasks.put(SocioSeerConstant.POST_TASK_REJECT, rejectedTaskCount);
		int countTotal = pendingTaskCount+approvedTaskCount+rejectedTaskCount;
		if(!CollectionUtils.isEmpty(tasks)){
			countTasks.put(SocioSeerConstant.POST_TASK_SCHEDULED, countScheduledTask(tasks).size());
			countTotal+=countScheduledTask(tasks).size();
		}
		countTasks.put(ALL, countTotal);
		return countTasks;
	}

	/**
	 * <b>Get Task by postId</b>
	 * 
	 * @param postId
	 * @return Task
	 */
	@Override
	public Task getTaskByPostId(@NonNull String postId) {

		try {
			return taskDao.getTaskByPostId(postId);
		} catch (Exception e) {
			String message = String.format("Error while fetching tasks by post id : %s", postId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}

	}

	/**
	 * <b>Get Media Details by Task</b>
	 * 
	 * @param task
	 * @param mediaids
	 */
	private void getMediaDetails(Task task, List<String> mediaids) {

		try {
			List<Media> medias = new ArrayList<Media>();
			mediaids.forEach(id -> {
				Media media = mediaService.get(id);
				if (!ObjectUtils.isEmpty(media)) {
					medias.add(media);
				}
			});
			setImageUrl(medias);
			task.setMedias(medias);
		} catch (Exception e) {
			String message = String.format("Error while fetching media");
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Set Image url in medias</b>
	 * 
	 * @param medias
	 */
	private void setImageUrl(List<Media> medias) {
		medias.forEach(media -> {
			media.setUrl(urlUtil.getUrl(media.getClientId() + File.separator + media.getMediaType(),
					media.getHashFileName()));
		});
	}

	/**
	 * <b>Count All Todays Task</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @param userId
	 * @return returns List of Task
	 */
	@Override
	public List<Task> countAllTodayTasks(Pageable pageable, List<Filter> filters, String userId) {
		List<Task> tasks = mongoTemplate.find(
				QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.USER_APPROVER, userId), pageable),
				Task.class);
		if(CollectionUtils.isEmpty(tasks)){
		 tasks = countScheduledTask(tasks);
		}
		return tasks;
	}
	
	private List<Task> countScheduledTask(List<Task> tasks){
		List<Task> newTask = new ArrayList<Task>();
		for (Task task : tasks) {
			boolean status = false;
			Optional<List<PostSchedule>> postSchedules = postScheduleService.findByPostId(task.getPostId());
			if (postSchedules.isPresent()) {
				for (PostSchedule postSchedule : postSchedules.get()) {
					Date dt = null;
					dt = postSchedule.getRunAt();
					try {
						Date date = new Date();
						date = sdf.parse(sdf.format(date));
						dt = sdf.parse(sdf.format(dt));
						if (date.compareTo(dt) == 0) {
							status = true;
							break;
						}
					} catch (Exception e) {
						String message = "Error while convert date format";
						log.error(message);
						throw new SocioSeerException(message);
					}
				}
			}
			if (status) {
				createdByName(task);
				newTask.add(task);
			}
		}
		return newTask;
		
	}

	/**
	 * <b>Get cretaedBy name of Task</b>
	 * 
	 * @param task
	 */
	private void createdByName(Task task) {
		User user = userService.get(task.getCreatedBy());
		if (!ObjectUtils.isEmpty(user)) {
			task.setCreatedByName(user.getFullName());
		}
	}


	@Override
	public void deleteByPostId(@NonNull String postId) {
		try{
			taskDao.deleteByPostId(postId);
		}catch(Exception e) {
			String message = String.format("Error while deleting task by post id :%s",postId);
			log.error(message);
			throw new SocioSeerException(message);
	}
	}

}
