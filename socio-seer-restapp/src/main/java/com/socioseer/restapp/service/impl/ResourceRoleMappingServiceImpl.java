package com.socioseer.restapp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.socioseer.common.domain.ResourceRoleMapping;
import com.socioseer.common.domain.Role;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.ResourceRoleMappingDao;
import com.socioseer.restapp.service.api.ResourceRoleMappingService;
import com.socioseer.restapp.service.api.RoleService;
import com.socioseer.restapp.service.util.DateUtil;

/**
 * <h3>ResourceRoleMapping Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class ResourceRoleMappingServiceImpl implements ResourceRoleMappingService {

	@Autowired
	private ResourceRoleMappingDao resourceRoleMappingDao;

	@Autowired
	private RoleService roleService;

	/**
	 * <b>Save ResourceRoleMapping</b>
	 * 
	 * @param resourceRoleMapping
	 * @return returns ResourceRoleMapping
	 */
	@Override
	public ResourceRoleMapping save(@NonNull ResourceRoleMapping resourceRoleMapping) {
		validateResourceRoleMapping(resourceRoleMapping);
		try {
			resourceRoleMapping.setRoles(getRoles(resourceRoleMapping));
			resourceRoleMapping.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			resourceRoleMapping.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			return resourceRoleMappingDao.save(resourceRoleMapping);
		} catch (Exception e) {
			String message = String.format("Error while saving resourcerolemapping for with  resource name : %s",
					resourceRoleMapping.getResourceName());
			log.error(message, e);
			throw new SocioSeerException(message);
		}

	}

	/**
	 * <b>Update ResourceRoleMapping</b>
	 * 
	 * @param id
	 * @param resourceRoleMapping
	 * @return returns ResourceRoleMapping
	 */
	@Override
	public ResourceRoleMapping update(@NonNull String id, @NonNull ResourceRoleMapping resourceRoleMapping) {

		ResourceRoleMapping existingResourceRoleMapping = resourceRoleMappingDao.findOne(id);

		if (ObjectUtils.isEmpty(existingResourceRoleMapping)) {
			String message = String.format("No resoruce role mapping found for id : %s", id);
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		if (!existingResourceRoleMapping.getUrl().equals(resourceRoleMapping.getUrl())
				&& getResourceRoleMappingByUrl(resourceRoleMapping.getUrl()) != null) {
			String message = String.format("Duplicate URL : %s, hence cannot update", resourceRoleMapping.getUrl());
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		try {
			resourceRoleMapping.setRoles(getRoles(resourceRoleMapping));
			resourceRoleMapping.setCreatedDate(existingResourceRoleMapping.getCreatedDate());
			resourceRoleMapping.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			resourceRoleMapping.setId(id);
			return resourceRoleMappingDao.save(resourceRoleMapping);
		} catch (Exception e) {
			String message = String.format("Error while updating role url mapping with id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get ResourceRoleMapping by Id</b>
	 * 
	 * @param id
	 * @return returns ResourceRoleMapping
	 */
	@Override
	public ResourceRoleMapping get(@NonNull String id) {
		return resourceRoleMappingDao.findOne(id);
	}

	/**
	 * <b>Get ResourceRoleMapping by url</b>
	 * 
	 * @param url
	 * @return returns ResourceRoleMapping
	 */
	@Override
	public ResourceRoleMapping getResourceRoleMappingByUrl(String url) {
		return resourceRoleMappingDao.findOneByUrl(url);
	}

	/**
	 * <b>Get All ResourceRoleMapping</b>
	 * 
	 * @return returns List of ResourceRoleMapping
	 */
	@Override
	public List<ResourceRoleMapping> getAll() {
		return resourceRoleMappingDao.findAll();
	}

	/**
	 * <b>Validate ResourceRoleMapping</b>
	 * 
	 * @param resourceRoleMapping
	 * 
	 */
	private void validateResourceRoleMapping(ResourceRoleMapping resourceRoleMapping) {

		if (StringUtils.isEmpty(resourceRoleMapping.getUrl())) {
			log.info("URL cannot be empty/null.");
			throw new IllegalArgumentException("URL cannot be empty/null.");
		}

		if (StringUtils.isEmpty(resourceRoleMapping.getResourceName())) {
			log.info("Resource name cannot be empty/null.");
			throw new IllegalArgumentException("Resource name cannot be empty/null.");
		}

		if (CollectionUtils.isEmpty(resourceRoleMapping.getRoleNames())) {
			log.info("Role names cannot be empty/null.");
			throw new IllegalArgumentException("Role names cannot be empty/null.");
		}

		ResourceRoleMapping existingResourceRoleMapping = getResourceRoleMappingByUrl(resourceRoleMapping.getUrl());

		if (!ObjectUtils.isEmpty(existingResourceRoleMapping)) {
			String message = String.format("Duplicate URL : %s", resourceRoleMapping.getUrl());
			log.info(message);
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * <b>Get Roles of ResourceRoleMapping</b>
	 * 
	 * @param resourceRoleMapping
	 * @return returns List of Roles
	 */
	private List<Role> getRoles(ResourceRoleMapping resourceRoleMapping) {
		List<Role> roles = resourceRoleMapping.getRoleNames().stream().map(roleName -> {
			return roleService.getRoleByName(roleName);
		}).collect(Collectors.toList());
		return roles;
	}

}
