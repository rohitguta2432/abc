package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.domain.User;
import com.socioseer.common.dto.Filter;

/**
 * <h3>User Services</h3>
 * @author OrangeMantra
 * @since  JDK 1.8
 * @version 1.0
 *
 */
public interface UserService extends CrudApi<User> {

  /**
   * <b>Get users list by brandId and also get per page</b>	
   * @param brandId		brandId as String
   * @param pageable
   * @return			returns users list
   */
  Optional<List<User>> getUsersByBrandId(String brandId, Pageable pageable);

  /**
   * <b>Get users list by clientId and status</b>
   * @param clientId      clientId as String
   * @param status		status as integer
   * @return			returns users list
   */
  Optional<List<User>> getUserByclientIdAndStatus(String clientId, int status);

  /**
   * <b>Get users list by clientId</b>
   * @param clientId    clientId as String
   * @param pageable
   * @param filters		Filter Objects in list
   * @return			returns users list
   */
  List<User> getUsersByClientId(String clientId, Pageable pageable, List<Filter> filters);

  /**
   * <b>Save Users</b>
   * @param user			users list	
   * @param profilePicture	profilePicture as Multipart image File in list
   * @return				returns users list
   */
  List<User> save(List<User> user, List<MultipartFile> profilePicture);

  /**
   * <b>Update User</b>
   * @param userId				userId as String
   * @param user   				User Object
   * @param profilePicture      profilePicture as Multipart image File
   * @return					returns user
   */
  User update(String userId, User user, MultipartFile profilePicture);

  /**
   * <b>Get User by email</b>
   * @param email   email as String
   * @return		returns user
   */
  User getUserByEmail(String email);
  
  /**
   * <b>Create users for client</b>
   * @param user	users list
   * @return		returns users list
   */
  List<User> createUserForClientAccount(List<User> user);
  
  /**
   * <b>Get all users</b> 
   * @param pageable
   * @param filters		Filter Objects in list
   * @return			returns users list 
   */
  List<User> getAllUsers(Pageable pageable, List<Filter> filters);
  
  /**
   * <b>Delete User</b> 
   * @param id          id as String
   * @param updatedBy	updatedBy as String    
   */
  void delete(String id, String updatedBy);
  
  /**
   * <b>Change the user Status</b> 
   * @param id          id as String 
   * @param status      status as integer
   * @param updatedBy   updated by as String
   */
  void changeStatus(String id, int status, String updatedBy);
  
  /**
   * <b>Get users list by clientId </b>
   * @param clientId    clientId as String
   * @param pageable    
   * @param filters		Filter Objects in list 
   * @return			returns users list
   */
  List<User> getApproversByClientId(String clientId, Pageable pageable, List<Filter> filters);

}
