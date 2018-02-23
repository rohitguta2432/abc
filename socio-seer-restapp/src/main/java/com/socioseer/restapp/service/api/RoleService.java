package com.socioseer.restapp.service.api;

import java.util.List;

import com.socioseer.common.domain.Role;

/**
 * <h3>Role Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface RoleService extends CrudApi<Role> {

	/**
	 * <b>Get Role by Name</b>
	 * @param roleName
	 *            roleName as String
	 * @return returns Role Object
	 */
	Role getRoleByName(String roleName);

	/**
	 *  <b>Get All Role</b>
	 * @return returns List of Role Object
	 */
	List<Role> getAllRoles();

	/**
	 * <b>Delete Role</b>
	 * @param id
	 *            id as String
	 * @param updateBy
	 *            updateBy as String
	 * @return returns List of Role Object
	 */
	void delete(String id, String updateBy);

	/**
	 *  <b>Count All Role</b>
	 * @return returns int
	 */

	int countAll();

}
