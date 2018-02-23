package com.socioseer.restapp.dao.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.campaign.SocialPlatform;

/**
 * <h3>SocialHandler Dao</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface SocialHandlerDao extends PagingAndSortingRepository<SocialHandler, String> {

	/**
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param pageable
	 * @return returns List of SocialHandlers
	 */
	List<SocialHandler> findAllByClientId(String clientId, Pageable pageable);

	/**
	 * 
	 * @param handlerId
	 *            handlerId as String
	 * @param socialPlatform
	 *            socialPlatform details
	 * @return returns List of SocialHandlers
	 */
	Optional<SocialHandler> findByIdAndSocialPlatform(String handlerId, SocialPlatform socialPlatform);

	/**
	 * 
	 * @param clientId
	 *            clientId as String
	 * @param socialPlatform
	 *            socialPlatform details
	 * @return returns List of SocialHandlers
	 */
	List<SocialHandler> findAllByClientIdAndSocialPlatform(String clientId, SocialPlatform socialPlatform);

	/**
	 * 
	 * @param socialPlatform
	 *            socialPlatform details
	 * @return returns List of SocialHandlers
	 */
	List<SocialHandler> findAllBySocialPlatform(SocialPlatform socialPlatform);

	/**
	 * 
	 * @param socialPlatform
	 *            socialPlatform details
	 * @return returns List of SocialHandlers
	 */
	List<SocialHandler> findBySocialPlatform(SocialPlatform socialPlatform);
}
