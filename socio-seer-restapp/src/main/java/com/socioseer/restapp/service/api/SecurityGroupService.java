package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.SecurityGroup;
import com.socioseer.common.dto.Filter;

/**
 * <h3>SecurityGroup Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SecurityGroupService extends CrudApi<SecurityGroup> {

	/**
	 * <b>Get Security Group By clientId</b>
	 * 
	 * @param pageable
	 * @return List of SesurityGroups
	 */
	Optional<List<SecurityGroup>> getSecurityGroupByClientId(String clientId, Pageable pageable);

	/**
	 * <b>Get All SecurityGroups</b>
	 * 
	 * @return List of SesurityGroups
	 */
	List<SecurityGroup> getAllSecurityGroups();

	/**
	 * <b>Get SecurityGroup By Name</b>
	 * 
	 * @param name
	 *            name as String
	 * @return SesurityGroup Object
	 */
	SecurityGroup getSecurityGroupByName(String name);

	/**
	 * <b>Change Status of SecurityGroup</b>
	 * 
	 * @param id
	 *            id as String
	 * @param status
	 *            status as int
	 * @param updatedBy
	 *            updatedBy as String
	 */
	void changeStatus(String id, int status, String updatedBy);

	/**
	 * <b>Change Status of SecurityGroup</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @return List of SecurityGroup
	 */
	List<SecurityGroup> getAllSecurityActiveGroups(Pageable pageable, List<Filter> filters);

	/**
	 * <b>Get SecurityGroup By Name</b>
	 * 
	 * @param name
	 *            name as String
	 * @return SecurityGroup Object
	 */
	SecurityGroup getByName(String name);

}
