package com.socioseer.restapp.service.impl;

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

import com.socioseer.common.domain.SecurityGroup;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.SecurityGroupDao;
import com.socioseer.restapp.service.api.SecurityGroupService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>SecurityGroup Service Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class SecurityGroupServiceImpl implements SecurityGroupService {

	@Autowired
	private SecurityGroupDao securityGroupDao;

	@Autowired
	MongoTemplate mongoTemplate;

	/**
	 * <b>Save SecurityGroup</b>
	 * 
	 * @param securityGroup
	 * @return returns SecurityGroup
	 */
	@Override
	public SecurityGroup save(@NonNull SecurityGroup securityGroup) {

		validateSecurityGroup(securityGroup);
		Optional<SecurityGroup> existingSecurityGroup = securityGroupDao
				.findOneByClientIdAndName(securityGroup.getClientId(), securityGroup.getName());

		if (existingSecurityGroup.isPresent()) {
			String message = String.format("There already exists a security group with name : %s",
					securityGroup.getName());
			log.info(message);
			throw new IllegalArgumentException(message);
		}

		try {
			securityGroup.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
			securityGroup.setUpdatedDate(securityGroup.getCreatedDate());
			return securityGroupDao.save(securityGroup);
		} catch (Exception e) {
			String message = String.format("Error while saving security group for client id : %s",
					securityGroup.getClientId());
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Update SecurityGroup</b>
	 * 
	 * @param securityGroupId
	 * @param securityGroup
	 * @return returns SecurityGroup
	 */
	@Override
	public SecurityGroup update(@NonNull String securityGroupId, @NonNull SecurityGroup securityGroup) {

		validateSecurityGroup(securityGroup);

		try {
			securityGroup.setId(securityGroupId);
			securityGroup.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
			return securityGroupDao.save(securityGroup);
		} catch (Exception e) {
			String message = String.format("Error while updating security group with name : %s for client id : %s",
					securityGroup.getName(), securityGroup.getClientId());
			log.info(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get SecurityGroup by Id</b>
	 * 
	 * @param securityGroupId
	 * @return returns SecurityGroup
	 */
	@Override
	public SecurityGroup get(@NonNull String securityGroupId) {

		try {
			return securityGroupDao.findOne(securityGroupId);
		} catch (Exception e) {
			String message = String.format("Error while fetching security group with id : %s", securityGroupId);
			log.info(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get SecurityGroup by clientId</b>
	 * 
	 * @param clientId
	 * @param pageable
	 * @return returns List of SecurityGroup
	 */
	@Override
	public Optional<List<SecurityGroup>> getSecurityGroupByClientId(@NonNull String clientId, Pageable pageable) {

		try {
			return securityGroupDao.findAllByClientId(clientId, pageable);
		} catch (Exception e) {
			String message = String.format("Error while fetching security group for client id : %s", clientId);
			log.info(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get All SecurityGroup</b>
	 * 
	 * @return returns List of SecurityGroup
	 */
	@Override
	public List<SecurityGroup> getAllSecurityGroups() {

		try {
			return securityGroupDao.findAll();
		} catch (Exception e) {
			log.info("Error while fetching security groups", e);
			throw new SocioSeerException("Error while fetching security groups");
		}
	}

	/**
	 * <b>Get All Active SecurityGroup</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @return returns List of SecurityGroup
	 */
	@Override
	public List<SecurityGroup> getAllSecurityActiveGroups(Pageable pageable, List<Filter> filters) {

		try {
			Query query = QueryBuilder.createQuery(filters, pageable);
			return mongoTemplate.find(query, SecurityGroup.class);
		} catch (Exception e) {
			log.info("Error while fetching security groups", e);
			throw new SocioSeerException("Error while fetching security groups");
		}
	}

	/**
	 * <b>Get SecurityGroup by name</b>
	 * 
	 * @param name
	 * @return returns SecurityGroup
	 */
	@Override
	public SecurityGroup getSecurityGroupByName(@NonNull String name) {

		try {
			return securityGroupDao.findOneByName(name);
		} catch (Exception e) {
			String message = String.format("Error while fetching security group by name : %s", name);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Validate SecurityGroup</b>
	 * 
	 * @param securityGroup
	 * 
	 */
	private void validateSecurityGroup(SecurityGroup securityGroup) {

		if (StringUtils.isEmpty(securityGroup.getName())) {
			log.info("Security group name can not be null/empty");
			throw new IllegalArgumentException("Security group name can not be null/empty");
		}

		if (StringUtils.isEmpty(securityGroup.getCreatedBy())) {
			log.info("Created by user id can not be null/empty");
			throw new IllegalArgumentException("Created by user id can not be null/empty");
		}

		if (CollectionUtils.isEmpty(securityGroup.getRoles())) {
			log.info("Roles list cannot be null/empty.");
			throw new IllegalArgumentException("Roles list cannot be null/empty.");
		}
	}

	/**
	 * <b>Change Status of SecurityGroup</b>
	 * 
	 * @param id
	 * @param status
	 * @param updatedBy
	 * 
	 */
	@Override
	public void changeStatus(@NonNull String id, int status, @NonNull String updatedBy) {
		if (status < 0) {
			String message = String.format("Invalid status %s", status);
			log.info(message);
			throw new IllegalArgumentException(message);
		}
		try {
			SecurityGroup securityGroup = securityGroupDao.findOne(id);
			if (ObjectUtils.isEmpty(securityGroup)) {
				String message = String.format("Security group not found by id %s", id);
				log.info(message);
				throw new IllegalArgumentException(message);
			}
			securityGroup.setStatus(status);
			securityGroup.setUpdatedBy(updatedBy);
			securityGroupDao.save(securityGroup);
		} catch (Exception e) {
			String message = String.format("Error while fetching security group by id : %s", id);
			log.error(message, e);
			throw new SocioSeerException(message);
		}
	}

	/**
	 * <b>Get SecurityGroup by Name</b>
	 * 
	 * @param name
	 * @return returns SecurityGroup
	 */
	@Override
	public SecurityGroup getByName(@NonNull String name) {

		try {
			return securityGroupDao.findOneByName(name);
		} catch (Exception e) {
			String message = String.format("Error while fetching security group with name : %s", name);
			log.info(message, e);
			throw new SocioSeerException(message);
		}
	}

}
