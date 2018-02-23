package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.Alert;
import com.socioseer.common.dto.Filter;

/** 
 * <h3>Alert Services</h3>
 * @author OrangeMantra
 * @since  JDK 1.8
 * @version 1.0
 *
 */
public interface AlertService extends CrudApi<Alert> {
	
  /**
   * <b>Get Notification count of User</b>	
   * @param userId    
   * @return 		returns integer data
   */
  int getCountByUserId(String userId);

  /**
   * <b>Update Notification of User</b>
   * @param userId
   * @return		returns boolean data
   */
  boolean updateNotificationStatus(String userId);

  /**
   * <b>Delete Notification</b>
   * @param id
   * @param updatedBy	  
   */
  void delete(String id, String updatedBy);

  /**
   * <b>Get All Notifications</b>
   * @param pageable
   * @param filters		Filter List
   * @return			returns Alert list
   */
  List<Alert> getAll(Pageable pageable, List<Filter> filters);
  
  /**
   * <b>Get all not viewed notifications count per User</b>
   * @param userId
   * @param pageable
   * @return			returns map object
   */
  Map<String, Integer> getAllNotViewed(String userId, Pageable pageable);
  
  /**
   * <b>Get Notifications of User</b>
   * @param userId
   * @param pageable
   * @param filters
   * @return			returns Alert list
   */
  List<Alert> getNotificationByUserId(String userId, Pageable pageable,
      List<Filter> filters);

  /**
   * <b>Valkidate Facebook Token for Alert </b>
   * @param	 userId	
   * @return returns Map<String,String>
   */
  Map<String, String> validateFacebookToken(String userId);

}
