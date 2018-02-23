package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.campaign.SocialPlatform;
import com.socioseer.common.dto.Filter;

/**
 * <h3>SocialHandler Services</h3>
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SocialHandlerService extends CrudApi<SocialHandler> {

	/**
	 * <b>Save SocialHandler</b>
	 * 
	 * @param socialHandlers
	 *            socialHandlers as List
	 * @return returns List of SocialHandlers.
	 */
	public List<SocialHandler> save(List<SocialHandler> socialHandlers);

	/**
	 * <b>Get SocialHandler By clientId</b>
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param pageable
	 * @return returns List of SocialHandlers.
	 */
	public List<SocialHandler> getSocialHandlerByClientId(String clientId, Pageable pageable);

	/**
	 * <b>Get SocialHandlers</b>
	 * 
	 * @return returns List of SocialHandlers.
	 */
	public List<SocialHandler> getSocialHandlers();

	public void updateHandlerLastFetch(Map<String, String> data);

	/**
	 * <b>Get All SocialHandlers</b>
	 * 
	 * @param pageable
	 * @param filters
	 * @return returns List of SocialHandlers.
	 */
	List<SocialHandler> getAll(Pageable pageable, List<Filter> filters);

	/**
	 * <b>Get SocialHandler By Client Id and Social PlateForm Id</b>
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param socialPlatform
	 * @return returns List of SocialHandlers.
	 */
	public List<SocialHandler> getSocialHandlerByClientIdAndSocialPlatform(String clientId,
			SocialPlatform socialPlatform);

	/**
	 * <b>Get SocialHandler By Client Id and Post Id</b>
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param platformId
	 *            platformId as String
	 * @param pageable
	 * @return returns List of SocialHandlers.
	 */
	public List<SocialHandler> getSocialHandlerByClientIdAndPostId(String clientId, String platformId,
			Pageable pageable);

	/**
	 * <b>Delete SocialHandler By Id</b>
	 * 
	 * @param id
	 *            id as String
	 * @param updatedBy
	 *            updatedBy as String
	 */
	public void delete(String id, String updatedBy);

	/**
	 * <b>Validate FaceBook Handles Token</b>
	 */
	public void validateFacebookHandlerTokens();
	
	/**
	 * <b>Get SocialHandler By Social Plateform</b>
	 * 
	 * @param socialPlatform
	 * @return List of SocialHandlers.
	 */
	public List<SocialHandler> findBySocialPlatform(SocialPlatform socialPlatform);

}
