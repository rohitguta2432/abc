package com.socioseer.restapp.dao.api;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.Role;

/**
 * <h3>Role Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface RoleRepository extends MongoRepository<Role, String> {

	/**
	 * 
	 * @param name
	 *            name as String
	 * @return returns Role Object
	 */
	Role findOneByName(String name);

	/**
	 * 
	 * @param isDefault
	 *            isDefault as boolean
	 * @param isAdmin
	 *            isAdmin as boolean
	 * @return returns List of Roles
	 */
	List<Role> findAllByIsDefaultAndIsAdmin(boolean isDefault, boolean isAdmin);

	/**
	 * 
	 * @param isAdmin
	 *            isAdmin as boolean
	 * @return returns List of Roles
	 */
	List<Role> findAllByIsAdmin(boolean isAdmin);

}
