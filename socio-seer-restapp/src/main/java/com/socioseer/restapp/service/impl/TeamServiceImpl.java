package com.socioseer.restapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.NotificationMeaage;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.constants.ValidationConstants;
import com.socioseer.common.domain.Team;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Alert;
import com.socioseer.common.domain.model.Client;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.TeamDao;
import com.socioseer.restapp.exception.ValidationException;
import com.socioseer.restapp.service.api.AlertService;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.api.TeamService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;
import com.socioseer.restapp.service.util.RoleUtil;

/**
 * <h3>Team Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	private TeamDao teamDao;

	@Autowired
	private UserService userService;

	@Autowired
	private ClientService clientService;

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	private AlertService notificationService;

	/**
	 * <b>Save Team</b>
	 * 
	 * @param team
	 * @return returns Team
	 */
	@Override
	public Team save(@NonNull Team team) {

		validateTeam(team);
		if (!CollectionUtils.isEmpty(team.getContentApproversList())) {
			validateApproverRole(team.getContentApproversList());
		}
		try {
			team.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			team.setUpdatedDate(team.getCreatedDate());
			createNotification(team);
			return teamDao.save(team);
		} catch (Exception e) {
			String message = String.format("Error while saving team for client id : %s", team.getClientId());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update Team</b>
	 * 
	 * @param teamId
	 * @param team
	 * @return returns Team
	 */
	@Override
	public Team update(@NonNull String teamId, @NonNull Team team) {

		validateTeam(team);
		Team existingTeam = teamDao.findOne(teamId);

		if (ObjectUtils.isEmpty(existingTeam)) {
			String message = String.format("No team found with team id : %s", teamId);
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		try {
			team.setId(teamId);
			team.setCreatedDate(existingTeam.getCreatedDate());
			team.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			return teamDao.save(team);
		} catch (Exception e) {
			String message = String.format("Error while updating team by team id : %s", teamId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get Team by teamId</b>
	 * 
	 * @param teamId
	 * @return returns Team
	 * 
	 */
	@Override
	public Team get(@NonNull String teamId) {
		try {
			Team team = teamDao.findOne(teamId);

			team.setNoOfUsers(numberOfUsers(team));
			if (!CollectionUtils.isEmpty(team.getUsersList())) {
				List<User> userList = new ArrayList<User>();
				team.getUsersList().forEach(userId -> {
					User user = userService.get(userId);
					User userTeam = new User();
					if (!ObjectUtils.isEmpty(user)) {
						userTeam.setId(user.getId());
						userTeam.setFullName(user.getFullName());
						userList.add(userTeam);
					}
				});
				team.setUsers(userList);
			}
			if (!CollectionUtils.isEmpty(team.getContentApproversList())) {
				List<User> userList = new ArrayList<User>();
				team.getContentApproversList().forEach(approverId -> {
					User user = userService.get(approverId);
					User userTeam = new User();
					if (!ObjectUtils.isEmpty(user)) {
						userTeam.setId(user.getId());
						userTeam.setFullName(user.getFullName());
						userList.add(userTeam);
					}
				});
				team.setContentApprovers(userList);
			}
			team.setNoOfUsers(numberOfUsers(team));
			return team;
		} catch (Exception e) {
			String message = String.format("Error while fetching team by team id : %s", teamId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get Team by clientId</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @param clientId
	 * @return returns List of Team
	 */
	@Override
	public List<Team> getTeamByClientId(Pageable pageable, List<Filter> filters, String clientId) {
		try {
			Query query = QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.CLIENT_ID, clientId),
					pageable);
			List<Team> teams = mongoTemplate.find(query, Team.class);
			teams.forEach(team -> {
				team.setNoOfUsers(numberOfUsers(team));
			});
			getTeamsName(teams);
			return teams;
		} catch (Exception e) {
			String message = String.format("Error while fetching team by team id : %s", clientId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get Team Name</b>
	 * 
	 * @param teams
	 */
	private void getTeamsName(List<Team> teams) {

		teams.forEach(team -> {

			if (!CollectionUtils.isEmpty(team.getUsersList())) {
				List<User> userList = new ArrayList<User>();
				team.getUsersList().forEach(userId -> {
					User user = userService.get(userId);
					User userTeam = new User();
					if (!ObjectUtils.isEmpty(user)) {
						userTeam.setId(user.getId());
						userTeam.setFullName(user.getFullName());
						userList.add(userTeam);
					}
				});
				team.setUsers(userList);
			}

			if (!CollectionUtils.isEmpty(team.getContentApproversList())) {
				List<User> userList = new ArrayList<User>();
				team.getContentApproversList().forEach(approverId -> {
					User user = userService.get(approverId);
					User userTeam = new User();
					if (!ObjectUtils.isEmpty(user)) {
						userTeam.setId(user.getId());
						userTeam.setFullName(user.getFullName());
						userList.add(userTeam);
					}
				});
				team.setContentApprovers(userList);
			}
		});
	}

	/**
	 * <b>Get Team by Team Name</b>
	 * 
	 * @param name
	 * @return returns Team
	 */

	@Override
	public Optional<Team> getTeamByName(@NonNull String name) {
		try {
			Optional<Team> team = teamDao.findOneByName(name);
			team.get().setNoOfUsers(numberOfUsers(team.get()));
			return team;
		} catch (Exception e) {
			String message = String.format("Error while fetching team by team name : %s", name);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate Team</b>
	 * 
	 * @param team
	 */
	private void validateTeam(Team team) {

		if (StringUtils.isEmpty(team.getClientId())) {
			log.info("Client id can not be empty/null");
			throw new IllegalArgumentException("Client id can not be empty/null");
		}

		if (StringUtils.isEmpty(team.getName())) {
			log.info("Team name can not be empty/null");
			throw new IllegalArgumentException("Team name can not be empty/null");
		}

		if (StringUtils.isEmpty(team.getCreatedBy())) {
			log.info("User id name can not be empty/null");
			throw new IllegalArgumentException("User id  name can not be empty/null");
		}

		if (CollectionUtils.isEmpty(team.getUsersList())) {
			log.info("Users list can not be empty/null");
			throw new IllegalArgumentException("Users list can not be empty/null");
		}

		if (CollectionUtils.isEmpty(team.getContentApproversList())) {
			log.info("Aprovers list can not be empty/null");
			throw new IllegalArgumentException("Users list can not be empty/null");
		}

		Client client = clientService.get(team.getClientId());
		if (ObjectUtils.isEmpty(client)) {
			String message = String.format("No client found with id : %s", team.getClientId());
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Validate Approver Role</b>
	 * 
	 * @param userIds
	 */
	private void validateApproverRole(List<String> userIds) {
		userIds.forEach(userId -> {
			User user = userService.get(userId);
			if (!RoleUtil.doUserHaveRole(user.getSecurityGroups(), ModelConstants.ROLE_CONTENT_APPROVER)) {
				String message = String.format("%s do not have roles to approve content", user.getEmail());
				log.error(message);
				throw new ValidationException(message, ValidationConstants.NO_ROLE);
			}
		});
	}

	/**
	 * <b>Delete Team</b>
	 * 
	 * @param id
	 * @param updatedBy
	 * 
	 */
	@Override
	public void delete(String id, String updatedBy) {

		try {
			Team team = teamDao.findOne(id);
			validateTeamDelete(team, id);
			team.setStatus(StatusConstants.DELETED);
			team.setUpdatedBy(updatedBy);
			team.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			team = teamDao.save(team);
			String message = String.format("Team deleted by team id %s", updatedBy);
			log.info(message);
		} catch (Exception e) {
			String message = String.format("Error while fetching team by team id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}

	}

	/**
	 * <b>Validate Team for Delete</b>
	 * 
	 * @param team
	 * @param id
	 */
	private void validateTeamDelete(Team team, String id) {
		if (ObjectUtils.isEmpty(team)) {
			String message = String.format("Error team not found by team id : %s", id);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		if (team.getStatus() == StatusConstants.DELETED) {
			String message = String.format("Error team already deleted");
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Change Team status</b>
	 * 
	 * @param id
	 * @param status
	 * @param updatedBy
	 */
	@Override
	public void changeStatus(@NonNull String id, int status, @NonNull String updatedBy) {
		if (status < 0) {
			String message = String.format("Invalid status %s", status);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		try {
			Team team = teamDao.findOne(id);
			if (ObjectUtils.isEmpty(team)) {
				String message = String.format("Team not found by id %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			team.setStatus(status);
			team.setUpdatedBy(updatedBy);
			teamDao.save(team);
		} catch (Exception e) {
			String message = String.format("Error while fetching team by id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}

	}

	/**
	 * <b>Get All Teams</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @return returns List of Team
	 */
	@Override
	public List<Team> getAllTeams(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			List<Team> teams = mongoTemplate.find(query, Team.class);
			teams.forEach(team -> {
				team.setNoOfUsers(numberOfUsers(team));
				if (!StringUtils.isEmpty(team.getClientId())) {
					team.setClientName(clientService.get(team.getClientId()).getClientName());
				}
			});
			getTeamsName(teams);
			return teams;
		} catch (Exception e) {
			log.error("Error while fetching teams.", e);
			throw new SocioSeerException("Error while fetching teams.");
		}
	}

	/**
	 * <b>Count No of Users in Team</b>
	 * 
	 * @param team
	 * @return returns int
	 */
	private int numberOfUsers(Team team) {
		int count = 0;
		if (!ObjectUtils.isEmpty(team)) {
			List<String> usres = team.getUsersList();
			count = usres.size();
			for (String value : team.getContentApproversList()) {
				if (!usres.contains(value))
					count++;
			}
		}
		log.info("Numbers of users in team :" + count);
		return count;
	}

	/**
	 * <b>Create Notification</b>
	 * 
	 * @param team
	 */
	private void createNotification(Team team) {

		team.getUsersList().forEach(s -> {
			boolean isApprover = false;
			if (team.getContentApproversList().contains(s)) {
				isApprover = true;
			}
			if (isApprover) {
				createAlert(team, ModelConstants.NOTIFICATION_TEAM, NotificationMeaage.MESSAGE_FOR_TEAM_APPROVED,
						NotificationMeaage.DESCRIPTION_FOR_TEAM_APPROVER);
			} else {
				createAlert(team, ModelConstants.NOTIFICATION_TEAM, NotificationMeaage.MESSAGE_FOR_TEAM_USER,
						NotificationMeaage.DESCRIPTION_FOR_TEAM_USER);
			}
		});
	}

	/**
	 * <b>Create Alert</b>
	 * 
	 * @param team
	 * @param type
	 * @param meaages
	 * @param description
	 */
	private void createAlert(Team team, String type, String meaages, String description) {
		try {
			Alert notofication = new Alert();
			notofication.setCreatedBy(team.getCreatedBy());
			notofication.setUpdatedBy(team.getCreatedBy());
			notofication.setUserId(team.getCreatedBy());
			notofication.setNotificationType(type);
			notofication.setMessage(meaages);
			notofication.setDescription(description);
			notificationService.save(notofication);
		} catch (Exception e) {
			log.error("Error while saving alert", e);
			throw new SocioSeerException("Error while saving alert.");
		}
	}

	/**
	 * <b>Get All Team</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @return returns List of Team
	 * 
	 */
	@Override
	public List<Team> getAll(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			List<Team> teams = mongoTemplate.find(query, Team.class);
			return teams;
		} catch (Exception e) {
			log.error("Error while team list", e);
			throw new SocioSeerException("Error while team list");
		}
	}

}
