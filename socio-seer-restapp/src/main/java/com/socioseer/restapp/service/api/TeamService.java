package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.Team;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Team Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface TeamService extends CrudApi<Team> {

	/**
	 * <b>Get Team by teamName</b>
	 * 
	 * @param teamName   teamName as String
	 *          
	 * @return returns team
	 */

	Optional<Team> getTeamByName(String teamName);

	/**
	 * <b>Delete Team by id</b>
	 * 
	 * @param id         id must be AlphaNumeric. 
	 * @param updateBy   updateBy must be AlphaNumeric.
	 *          
	 * @return returns   do not return anything
	 */
	void delete(String id, String updateBy);

	/**
	 * <b>Get Team by ClientId</b>
	 * 
	 * @param pageable   
	 * @param filters   
	 * @param clientId   clientId must be AlphaNumeric.
	 *          
	 * @return returns   List of Team Object.
	 */
	List<Team> getTeamByClientId(Pageable pageable, List<Filter> filters, String clientId);

	/**
	 * <b>Get All Team</b>
	 * 
	 * @param pageable   
	 * @param filters   
	 *          
	 * @return returns   List of Team Object.
	 */
	List<Team> getAllTeams(Pageable pageable, List<Filter> filters);
    
	/**
	 * <b>Its not in Use</b>
	 */
	List<Team> getAll(Pageable pageable, List<Filter> filters);

	/**
	 * <b>Change Status of Team</b>
	 * 
	 * @param id          id must be AlphaNumeric.
	 * @param status      status must be integer and in {1,2,3}.
	 * @param updatedBy   updatedBy must be AlphaNumeric.
	 *          
	 * @return returns    List of Team Object.
	 */
	void changeStatus(String id, int status, String updatedBy);

}
