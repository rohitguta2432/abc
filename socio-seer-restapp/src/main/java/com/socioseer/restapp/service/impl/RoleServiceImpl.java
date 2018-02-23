package com.socioseer.restapp.service.impl;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.Role;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.RoleRepository;
import com.socioseer.restapp.service.api.RoleService;
import com.socioseer.restapp.service.util.DateUtil;

/**
 * <h3>Role Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	public static String MANAGE_ROLE = "MANAGE";

	/**
	 * <b>Save Role</b>
	 * 
	 * @param role
	 * @return Role
	 */
	@Override
	public Role save(@NonNull Role role) {
		validateRole(role);
		try {
			role.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			role.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			return roleRepository.save(role);
		} catch (Exception e) {
			String message = String.format("Error while saving role for with  name : %s", role.getName());
			log.error(message, e);
			throw new SocioSeerException(message);
		}

	}

	/**
	 * <b>Update Role</b>
	 * 
	 * @param roleId
	 * @param role
	 * @return returns Role
	 */
	@Override
	public Role update(@NonNull String roleId, @NonNull Role role) {

		Role existingRole = roleRepository.findOne(roleId);

		if (ObjectUtils.isEmpty(existingRole)) {
			String message = String.format("No role found with id : %s", roleId);
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		if (!role.getName().equals(existingRole.getName()) && getRoleByName(role.getName()) != null) {
			String message = String.format("Duplicate role name : %s", roleId);
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		role.setId(roleId);
		role.setCreatedDate(existingRole.getCreatedDate());
		role.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
		return roleRepository.save(role);
	}

	/**
	 * <b>Get Role by roleId</b>
	 * 
	 * @param roleId
	 * @return returns Role
	 */
	@Override
	public Role get(@NonNull String roleId) {
		return roleRepository.findOne(roleId);
	}

	/**
	 * <b>Get Role by Name</b>
	 * 
	 * @param roleName
	 * @return returns Role
	 */
	@Override
	public Role getRoleByName(@NonNull String roleName) {
		return roleRepository.findOneByName(roleName);
	}

	/**
	 * <b>Get All Role</b>
	 * 
	 * @return returns List of Roles
	 */
	@Override
	public List<Role> getAllRoles() {
		return getAllDefaultRoles();
		// return roleRepository.findAll();
	}

	/**
	 * <b>Get All Default Role</b>
	 * 
	 * @return returns List of Roles
	 */
	private List<Role> getAllDefaultRoles() {
		return roleRepository.findAllByIsDefaultAndIsAdmin(false, false);
	}

	/**
	 * <b>Validate Role</b>
	 * 
	 * @param role
	 * 
	 */
	private void validateRole(Role role) {

		if (StringUtils.isEmpty(role.getName())) {
			log.info("Role name cannot be empty/null");
			throw new IllegalArgumentException("Role name cannot be empty/null");
		}

		if (getRoleByName(role.getName()) != null) {
			String message = String.format("There already exists a role with name : %s", role.getName());
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Delete Role</b>
	 * 
	 * @param id
	 * @param updatedBy
	 * 
	 */
	@Override
	public void delete(String id, String updatedBy) {

		try {
			Role role = roleRepository.findOne(id);
			validateRoleDelete(role, id);
			role.setStatus(StatusConstants.DELETED);
			role.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			role.setUpdatedBy(updatedBy);
			role = roleRepository.save(role);
			String message = String.format("Role deleted by role id %s", updatedBy);
			log.info(message);
		} catch (Exception e) {
			String message = String.format("Error while fetching role by team id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate Role for Delete</b>
	 * 
	 * @param role
	 * @param id
	 * 
	 */
	private void validateRoleDelete(Role role, String id) {
		if (ObjectUtils.isEmpty(role)) {
			String message = String.format("Error role not find by role id : %s", id);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		if (role.getStatus() == StatusConstants.DELETED) {
			String message = String.format("Error role already deleted");
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Count All Role</b>
	 * 
	 * @return returns int
	 */
	@Override
	public int countAll() {
		try {
			return (int) roleRepository.count();
		} catch (Exception e) {
			String message = String.format("Error while count all roles");
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

}
