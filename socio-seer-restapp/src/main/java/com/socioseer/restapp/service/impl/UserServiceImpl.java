package com.socioseer.restapp.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableMap;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.Role;
import com.socioseer.common.domain.SecurityGroup;
import com.socioseer.common.domain.User;
import com.socioseer.common.domain.model.Client;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.AuthTokenDao;
import com.socioseer.restapp.dao.api.RoleRepository;
import com.socioseer.restapp.dao.api.UserDao;
import com.socioseer.restapp.service.api.BrandService;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.api.SecurityGroupService;
import com.socioseer.restapp.service.api.TeamService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.EncryptionUtil;
import com.socioseer.restapp.service.util.FileUtility;
import com.socioseer.restapp.service.util.QueryBuilder;
import com.socioseer.restapp.util.QueryParser;
import com.socioseer.restapp.util.UrlUtil;
import com.socioseer.common.domain.Team;

/**
 * <h3>User Service Implementation</h3>
 * @author OrangeMantra
 * @since  JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private AuthTokenDao authTokenDao;

	@Autowired
	private BrandService brandService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private EncryptionUtil encryptionUtil;

	@Autowired
	private FileUtility fileUtility;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SecurityGroupService securityGroupService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UrlUtil urlUtil;

	@Autowired
	private TeamService teamService;

	private final String folderLocation = SocioSeerConstant.FOLDER_USER;

	/**
	 * <b>Save User</b>
	 * 
	 * @param user
	 * @return returns User
	 */
	@Override
	public User save(@NonNull User user) {
		User existingUser = getUserByEmail(user.getEmail());
		if (!ObjectUtils.isEmpty(existingUser)) {
			String message = String.format("There already exists an user with email : %s", user.getEmail());
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		try {
			user.setPassword(encryptionUtil.encode(user.getPassword()));
			user.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			user.setUpdatedDate(user.getCreatedDate());
			user.setEmail(user.getEmail().toLowerCase());
			clientUserCounUpdate(user);
			return userDao.save(user);
		} catch (Exception e) {
			String message = String.format("Error while saving user with email : %s", user.getEmail());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Save User</b>
	 * 
	 * @param users
	 * @param profilePictures
	 * @return returns List of Users
	 */
	@Override
	public List<User> save(@NonNull List<User> users, List<MultipartFile> profilePictures) {

		List<User> saveUsers = new ArrayList<>();
		for (User user : users) {
			MultipartFile profilePicture = null;
			if (!CollectionUtils.isEmpty(profilePictures)) {
				if (profilePictures.size() == 1) {
					profilePicture = profilePictures.get(0);
				}
			}
			validateUser(user);
			setDefaultRoles(user);
			canCreateMoreUser(user);
			String hashedFileName = null;
			try {
				if (profilePicture != null && !profilePicture.isEmpty()) {
					hashedFileName = fileUtility.getFileName(profilePicture.getOriginalFilename());
					fileUtility.saveFile(profilePicture, SocioSeerConstant.FOLDER_USER, user.getClientId(),
							hashedFileName);
					user.setProfileImageName(profilePicture.getOriginalFilename());
					user.setHashedProfileImageName(hashedFileName);
				}
				user.setEmail(user.getEmail().toLowerCase());
				getFullName(user);
				saveUsers.add(save(user));

			} catch (Exception e) {
				fileUtility.deleteFile(SocioSeerConstant.FOLDER_USER, user.getClientId(), hashedFileName);
				throw e;
			}
		}
		return saveUsers;

	}

	/**
	 * <b>Set Default Role to User</b>
	 * 
	 * @param user
	 * 
	 */
	private void setDefaultRoles(User user) {
		List<SecurityGroup> securityGroupList = user.getSecurityGroups();
		if (!CollectionUtils.isEmpty(securityGroupList)) {
			SecurityGroup securityGroup = securityGroupList.get(0);
			List<Role> roles = securityGroup.getRoles();
			List<Role> defaultRoles = roleRepository.findAllByIsDefaultAndIsAdmin(true, false);
			roles.addAll(defaultRoles);
			securityGroup.setRoles(roles);
			securityGroupList.clear();
			securityGroupList.add(securityGroup);
			user.setSecurityGroups(securityGroupList);
		}
	}

	/**
	 * <b>Update User</b>
	 * 
	 * @param userId
	 * @param user
	 * @param profilePicture
	 * @return returns User
	 */
	@Override
	public User update(@NonNull String userId, @NonNull User user, MultipartFile profilePicture) {

		String hashedFileName = null;
		try {
			User existingUser = userDao.findOne(userId);
			if (ObjectUtils.isEmpty(existingUser)) {
				String message = String.format("No user found with id : %s", userId);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			if (profilePicture != null && !profilePicture.isEmpty()
					&& !profilePicture.getOriginalFilename().equalsIgnoreCase(user.getProfileImageName())) {
				fileUtility.deleteFile(SocioSeerConstant.FOLDER_USER, existingUser.getClientId(),
						existingUser.getHashedProfileImageName());
				hashedFileName = fileUtility.getFileName(profilePicture.getOriginalFilename());
				fileUtility.saveFile(profilePicture, SocioSeerConstant.FOLDER_USER, existingUser.getClientId(),
						hashedFileName);
				user.setProfileImageName(profilePicture.getOriginalFilename());
				user.setHashedProfileImageName(hashedFileName);
			}
			user.setId(userId);
			user.setCreatedDate(existingUser.getCreatedDate());
			user.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			updateObject(user, existingUser);
			user.setPassword(encryptionUtil.encode(user.getPassword()));
			getFullName(existingUser);
			update(userId, existingUser);
			if (!ObjectUtils.isEmpty(existingUser) && !StringUtils.isEmpty(existingUser.getHashedProfileImageName()))
				existingUser.setProfileImageName(urlUtil.getUrl(folderLocation + File.separator + user.getClientId(),
						user.getHashedProfileImageName()));
			return existingUser;
		} catch (Exception e) {
			fileUtility.deleteFile(user.getClientId(), SocioSeerConstant.FOLDER_USER, hashedFileName);
			throw e;
		}

	}

	/**
	 * <b>Update User</b>
	 * 
	 * @param userId
	 * @param user
	 * @return returns User
	 */
	@Override
	public User update(@NonNull String userId, @NonNull User user) {
		try {
			if (user.getEmail() != null) {
				user.setEmail(user.getEmail().toLowerCase());

			}
			return userDao.save(user);
		} catch (Exception e) {
			String message = String.format("Error while updating user with id : %s for client id : %s", userId,
					user.getClientId());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get User by email</b>
	 * 
	 * @param email
	 * @return returns User
	 */
	@Override
	public User getUserByEmail(@NonNull String email) {

		try {
			User user = userDao.findOneByEmail(email);
			if (!ObjectUtils.isEmpty(user) && !StringUtils.isEmpty(user.getHashedProfileImageName()))
				user.setProfileImageName(urlUtil.getUrl(folderLocation + File.separator + user.getClientId(),
						user.getHashedProfileImageName()));
			return user;
		} catch (Exception e) {
			String message = String.format("Error while fetching user by email : %s", email);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get User by userId</b>
	 * 
	 * @param userId
	 * @return returns User
	 */
	@Override
	public User get(@NonNull String userId) {
		try {
			String nullPassword = null;
			User user = userDao.findOne(userId);
			if (!ObjectUtils.isEmpty(user) && (!StringUtils.isEmpty(user.getHashedProfileImageName()))) {
				user.setProfileImageName(urlUtil.getUrl(folderLocation + File.separator + user.getClientId(),
						user.getHashedProfileImageName()));

			}
			user.setPassword(nullPassword);
			user.setRePassword(nullPassword);
			Criteria criteria = new Criteria();
			criteria.orOperator(Criteria.where(ModelConstants.USERS_LIST).is(userId),
					Criteria.where(ModelConstants.APPROVER_NAME).is(userId));
			criteria.andOperator(Criteria.where(ModelConstants.TEAM_STATUS).is(StatusConstants.ACTIVE));
			Query query = new Query(criteria);
			List<Team> teams = mongoTemplate.find(query, Team.class);
			user.setTeams(teams);
			if (!StringUtils.isEmpty(user.getClientId())) {
				user.setClientName(this.getClientNameById(user.getClientId()));
			}
			return user;
		} catch (Exception e) {
			String message = String.format("Error while fetching user by id : %s", userId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate User</b>
	 * 
	 * @param user
	 */
	private void validateUser(User user) {

		if (StringUtils.isEmpty(user.getClientId())) {
			log.info("Client id can not be null/empty.");
			throw new IllegalArgumentException("Client id can not be null/empty.");
		}

		if (StringUtils.isEmpty(user.getClientName())) {
			log.info("Client name can not be null/empty.");
			throw new IllegalArgumentException("Client name can not be null/empty.");
		}

		if (StringUtils.isEmpty(user.getEmail())) {
			log.info("Email can not be null/empty.");
			throw new IllegalArgumentException("Email can not be null/empty.");
		}

		if (StringUtils.isEmpty(user.getPassword())) {
			log.info("Password can not be null/empty.");
			throw new IllegalArgumentException("Password can not be null/empty.");
		}

		if (StringUtils.isEmpty(user.getRePassword())) {
			log.info("Re password can not be null/empty.");
			throw new IllegalArgumentException("Re password can not be null/empty.");
		}

		if (!user.getPassword().equals(user.getRePassword())) {
			log.info("User password and re-password does not match.");
			throw new IllegalArgumentException("User password and re-password does not match.");
		}

		if (CollectionUtils.isEmpty(user.getSecurityGroups())) {
			log.info("Security groups can not be empty/null.");
			throw new IllegalArgumentException("Security groups cannot be empty/null.");
		}

		if (!StringUtils.isEmpty(user.getParentId())) {
			User parentUser = get(user.getParentId());
			if (ObjectUtils.isEmpty(parentUser)) {
				String message = String.format("No parent user found with id : %s", user.getParentId());
				log.info(message);
				throw new IllegalArgumentException(message);
			}
		}
	}

	/**
	 * } <b>Get User by clientId</b>
	 * 
	 * @param clientId
	 * @param pageable
	 * @param filters
	 * @return returns List of Users
	 */
	@Override
	public List<User> getUsersByClientId(@NonNull String clientId, Pageable pageable, List<Filter> filters) {
		try {
			List<User> users = mongoTemplate.find(
					QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.CLIENT_ID, clientId), pageable),
					User.class);
			appendUrl(users);
			return users;

		} catch (Exception e) {
			String message = String.format("Error while fetching users by client id : %s", clientId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Append User Profile url to User</b>
	 * 
	 * @param users
	 * @return returns List of Users
	 */
	private void appendUrl(List<User> users) {
		users.forEach(user -> {
			if (!ObjectUtils.isEmpty(user) && (!StringUtils.isEmpty(user.getHashedProfileImageName())))
				user.setProfileImageName(urlUtil.getUrl(folderLocation + File.separator + user.getClientId(),
						user.getHashedProfileImageName()));
		});
	}

	/**
	 * <b>Get User by brandId</b>
	 * 
	 * @param brandId
	 * @param pageable
	 * @return returns List of Users
	 */
	@Override
	public Optional<List<User>> getUsersByBrandId(@NonNull String brandId, Pageable pageable) {
		try {
			return userDao.findAllByBrandId(brandId, pageable);
		} catch (Exception e) {
			String message = String.format("Error while fetching users by brand id : %s", brandId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get User by clientId and status</b>
	 * 
	 * @param clientId
	 * @param status
	 * @return returns List of Users
	 */
	@Override
	public Optional<List<User>> getUserByclientIdAndStatus(@NonNull String clientId, int status) {
		try {
			return userDao.getUserByclientIdAndStatus(clientId, status);
		} catch (Exception e) {
			String message = String.format("Error while fetching users by client id : %s", clientId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Check User Limit for Client</b>
	 * 
	 * @param user
	 */
	private void canCreateMoreUser(User user) {

		Client client = clientService.get(user.getClientId());
		if (ObjectUtils.isEmpty(client)) {
			String message = String.format("Client id not found:  %s", user.getClientId());
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Count update for client users.</b>
	 * 
	 * @param user
	 */
	private void clientUserCounUpdate(User user) {
		Client client = clientService.get(user.getClientId());
		List<User> users = getUsersByClientId(client.getId(), null, null);
		int userCount = 0;
		if (!CollectionUtils.isEmpty(users)) {
			userCount = (int) users.stream().filter(usr -> usr.getStatus() != 3).count();
			if (++userCount > client.getNoOfUsers()) {
				String message = String.format("Cannot create more than %d users", client.getNoOfUsers());
				log.info(message);
				throw new IllegalArgumentException(message);
			}
		}
		updateUserCount(client);

	}

	/**
	 * <b>Update Object</b>
	 * 
	 * @param user
	 * @param existingUser
	 */
	private void updateObject(User user, User existingUser) {

		if (!StringUtils.isEmpty(user.getBrandId())) {
			if (ObjectUtils.isEmpty(brandService.get(user.getBrandId()))) {
				String message = String.format("No brand found with id : %s", user.getBrandId());
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			existingUser.setBrandId(user.getBrandId());
		}

		if (!StringUtils.isEmpty(user.getClientId())) {
			if (ObjectUtils.isEmpty(clientService.get(user.getClientId()))) {
				String message = String.format("Client is not found by  id %s", user.getClientId());
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			existingUser.setClientId(user.getClientId());
		}

		if (!StringUtils.isEmpty(user.getCreatedBy()))
			existingUser.setCreatedBy(user.getCreatedBy());

		if (!StringUtils.isEmpty(user.getHashedProfileImageName()))
			existingUser.setHashedProfileImageName(user.getHashedProfileImageName());

		if (!StringUtils.isEmpty(user.getFirstName()))
			existingUser.setFirstName(user.getFirstName());

		if (!StringUtils.isEmpty(user.getLastName()))
			existingUser.setLastName(user.getLastName());

		getFullName(existingUser);

		if (!StringUtils.isEmpty(user.getPhone()))
			existingUser.setPhone(user.getPhone());

		if (!StringUtils.isEmpty(user.getParentId())) {
			if (ObjectUtils.isEmpty(get(user.getParentId()))) {
				String message = String.format("User not found by parent id %s", user.getParentId());
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			existingUser.setParentId(user.getParentId());
		}

		if (!StringUtils.isEmpty(user.getPassword()) && !StringUtils.isEmpty(user.getRePassword())) {
			if (user.getPassword().equals(user.getRePassword())) {
				existingUser.setPassword(encryptionUtil.encode(user.getPassword()));
			} else {
				log.info("Password and confirm password missmatch.");
				throw new IllegalArgumentException("Password and confirm password missmatch");
			}
		}

		existingUser.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());

		if (!StringUtils.isEmpty(user.getProfileImageName()))
			existingUser.setProfileImageName(user.getProfileImageName());

		if (!StringUtils.isEmpty(user.getUpdatedBy()))
			existingUser.setUpdatedBy(user.getUpdatedBy());

		if (existingUser.getStatus() != user.getStatus())
			existingUser.setStatus(user.getStatus());
		compareSecurityGroups(user, existingUser);

	}

	/**
	 * <b>Compare SecurityGroup</b>
	 * 
	 * @param user
	 * @param existingUser
	 */
	private void compareSecurityGroups(User user, User existingUser) {

		if (!CollectionUtils.isEmpty(user.getSecurityGroups())) {

			List<SecurityGroup> securityGroups = new ArrayList<SecurityGroup>();

			List<SecurityGroup> exitedsecurityGroups = existingUser.getSecurityGroups();
			if (CollectionUtils.isEmpty(exitedsecurityGroups)) {
				existingUser.setSecurityGroups(user.getSecurityGroups());
			} else {
				int index = 0;
				for (SecurityGroup securityGroup : user.getSecurityGroups()) {
					SecurityGroup security = exitedsecurityGroups.get(index);

					if (ObjectUtils.isEmpty(security)) {
						continue;
					}

					if (!StringUtils.isEmpty(securityGroup.getName())) {
						security.setName(securityGroup.getName());
					}

					if (!StringUtils.isEmpty(securityGroup.getClientId())) {
						security.setClientId(securityGroup.getClientId());
					}

					if (!StringUtils.isEmpty(securityGroup.getCreatedBy()))
						security.setCreatedBy(securityGroup.getCreatedBy());

					if (!StringUtils.isEmpty(securityGroup.getUpdatedBy()))
						security.setUpdatedBy(securityGroup.getUpdatedBy());

					security.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());

					List<Role> roles = securityGroup.getRoles();
					List<Role> defaultRoles = roleRepository.findAllByIsDefaultAndIsAdmin(true, false);
					if (roles == null) {
						roles = new ArrayList<Role>();
					}
					roles.addAll(defaultRoles);
					security.setRoles(roles);
					index++;
					securityGroups.add(security);
				}
				existingUser.setSecurityGroups(securityGroups);
			}
		}
	}

	/**
	 * <b>Create User for Client</b>
	 * 
	 * @param users
	 * @return returns List of Users
	 */
	@Override
	public List<User> createUserForClientAccount(@NonNull List<User> users) {

		List<User> savedUsers = new ArrayList<>();
		try {

			users.forEach(user -> {

				if (StringUtils.isEmpty(user.getEmail())) {
					String message = String.format("Email should not be black or empty");
					log.info(message);
					throw new IllegalArgumentException(message);
				}

				User existingUser = getUserByEmail(user.getEmail());
				if (!ObjectUtils.isEmpty(existingUser)) {
					String message = String.format("There already exists an user with email : %s", user.getEmail());
					log.info(message);
					throw new IllegalArgumentException(message);
				}

				SecurityGroup clientSgGroup = securityGroupService
						.getSecurityGroupByName(ModelConstants.SG_CLIENT_ADMIN);
				user.setSecurityGroups(Arrays.asList(clientSgGroup));
				validateUser(user);
				user.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
				user.setUpdatedDate(user.getCreatedDate());

				user.setStatus(StatusConstants.ACTIVE);
				user.setPassword(encryptionUtil.encode(user.getPassword()));
				user.setRePassword(encryptionUtil.encode(user.getRePassword()));
				Client client = clientService.get(user.getClientId());
				SecurityGroup securityGroup = securityGroupService.getByName(SocioSeerConstant.CLIENT_ADMIN);
				if (!ObjectUtils.isEmpty(securityGroup)) {
					List<SecurityGroup> securityGroups = new ArrayList<SecurityGroup>();
					List<Role> roles = securityGroup.getRoles();
					List<Role> defaultRoles = roleRepository.findAllByIsDefaultAndIsAdmin(true, false);
					roles.addAll(defaultRoles);
					securityGroup.setRoles(roles);
					securityGroups.add(securityGroup);
					user.setSecurityGroups(securityGroups);
				} else {
					setDefaultRoles(user);
				}
				if (ObjectUtils.isEmpty(client)) {
					String message = String.format("Client id not found:  %s", user.getClientId());
					log.info(message);
					throw new IllegalArgumentException(message);
				}
				int count = client.getCountUsers();
				client.setCountUsers(++count);
				clientService.update(client.getId(), client);
				getFullName(user);
				if (user.getEmail() != null) {
					user.setEmail(user.getEmail().toLowerCase());
				}
				savedUsers.add(userDao.save(user));
			});

		} catch (Exception e) {
			String message = String.format("Error while creating admin users for client");
			log.error(message, e);
			throw new SocioSeerException(message);
		}
		return savedUsers;

	}

	/**
	 * <b>Get All Users</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @return returns List of Users
	 */
	@Override
	public List<User> getAllUsers(Pageable pageable, List<Filter> filters) {
		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			List<User> users = mongoTemplate.find(query, User.class);
			getTeamList(users);
			appendUrl(users);
			return users;
		} catch (Exception e) {
			log.error("Error while fetching users.", e);
			throw new SocioSeerException("Error while fetching users.");
		}
	}

	/**
	 * <b>Delete User</b>
	 * 
	 * @param id
	 * @param updatedBy
	 * 
	 */
	@Override
	public void delete(String id, String updatedBy) {

		try {
			User user = userDao.findOne(id);

			validateUserDelete(user, id);
			user.setStatus(StatusConstants.DELETED);
			user.setUpdatedBy(updatedBy);
			user.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			Client client = clientService.get(user.getClientId());
			if (ObjectUtils.isEmpty(client)) {
				String message = String.format("Client id not found:  %s", user.getClientId());
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			int count = client.getCountUsers();
			if (count > 0) {
				count = count - 1;
				client.setCountUsers(count);
			}
			clientService.update(client.getId(), client);
			update(user.getId(), user);
			authTokenDao.deleteByUserId(user.getId());
			log.info(String.format("User deleted by user id %s", updatedBy));
		} catch (Exception e) {
			String message = String.format("Error while deleting user by user id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate User for Delete</b>
	 * 
	 * @param user
	 * @param id
	 */
	private void validateUserDelete(User user, String id) {
		if (ObjectUtils.isEmpty(user)) {
			String message = String.format("Error user not find by user id : %s", id);
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		if (user.getStatus() == StatusConstants.DELETED) {
			String message = String.format("Error user already deleted");
			log.error(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Change User status</b>
	 * 
	 * @param id
	 * @param status
	 * @param updatedBy
	 *
	 */
	@Override
	public void changeStatus(String id, int status, String updatedBy) {
		if (status < 0) {
			String message = String.format("Invalid status %s", status);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		try {
			User user = userDao.findOne(id);
			Client client = clientService.get(user.getClientId());
			if (!ObjectUtils.isEmpty(client)) {
				if (client.getStatus() == 2) {
					String message = String.format("Related client is not Active");
					log.info(message);
					throw new IllegalArgumentException(message);
				}
			}
			if (ObjectUtils.isEmpty(user)) {
				String message = String.format("User not found by id %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			user.setStatus(status);
			user.setUpdatedBy(updatedBy);
			userDao.save(user);
		} catch (Exception e) {
			String message = String.format("Error while fetching user by id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update User count</b>
	 * 
	 * @param client
	 */
	private void updateUserCount(Client client) {
		int count = client.getCountUsers();
		client.setCountUsers(++count);
		clientService.update(client.getId(), client);
	}

	/**
	 * <b>Get Approver by clientId</b>
	 * 
	 * @param clientId
	 * @param pageable
	 * @param filters
	 */
	@Override
	public List<User> getApproversByClientId(@NonNull String clientId, Pageable pageable, List<Filter> filters) {
		try {
			List<User> users = mongoTemplate.find(
					QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.CLIENT_ID, clientId), pageable),
					User.class);
			return checkApprovers(users, clientId);
		} catch (Exception e) {
			String message = String.format("Error while fetching users by client id : %s", clientId);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Check Approvers</b>
	 * 
	 * @param users
	 * @param clientId
	 * @return returns List of Users
	 */
	private List<User> checkApprovers(List<User> users, @NonNull String clientId) {

		if (CollectionUtils.isEmpty(users)) {
			String message = String.format("Error while fetching users by client id %s:", clientId);
			log.info(message);
			throw new SocioSeerException(message);
		}
		List<User> approversUsers = new ArrayList<User>();
		users.forEach(user -> {
			if (user.getStatus() != StatusConstants.DELETED) {
				List<SecurityGroup> securityGroups = user.getSecurityGroups();
				securityGroups.forEach(securityGroup -> {
					List<Role> roles = securityGroup.getRoles();
					roles.forEach(role -> {
						if (role.getName().equals(ModelConstants.ROLE_CONTENT_APPROVER)) {
							approversUsers.add(user);
						}
					});
				});
			}
		});
		return approversUsers;
	}

	/**
	 * <b>Get Full Name</b>
	 * 
	 * @param user
	 */
	private void getFullName(User user) {
		try {
			String fullName = null;

			if (!StringUtils.isEmpty(user.getFirstName())) {
				user.setFirstName(convertTitleCase(user.getFirstName()));
				fullName = user.getFirstName();
			}
			if (!StringUtils.isEmpty(user.getLastName())) {
				user.setLastName(convertTitleCase(user.getLastName()));
				fullName += " " + user.getLastName();
			}
			user.setFullName(fullName);
		} catch (Exception e) {
			String message = String.format("Error while merge fullname of user by user id %s:", user.getId());
			log.info(message);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Convert to Title Case</b>
	 * 
	 * @param word
	 * @return returns String
	 */
	private String convertTitleCase(String word) {
		return WordUtils.capitalizeFully(word);
	}

	/**
	 * <b>Get Client Name by clientId</b>
	 * 
	 * @param clientId
	 * @return returns String
	 */

	private String getClientNameById(@NonNull String clientId) {
		Client client = clientService.get(clientId);
		if (ObjectUtils.isEmpty(client)) {
			String message = String.format("Client id not found:  %s", clientId);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		return client.getClientName();
	}

	/**
	 * <b>Get Team List</b>
	 * 
	 * @param users
	 */
	private void getTeamList(List<User> users) {
		if (!CollectionUtils.isEmpty(users)) {
			users.forEach(user -> {
				String query = ModelConstants.USERS_LIST + ":" + user.getId();
				List<Team> teams = teamService.getAllTeams(null, QueryParser.parse(query));
				user.setTeams(teams);
			});
		}
	}

}
