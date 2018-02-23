package com.socioseer.restapp.dao.api;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.Team;

/**
 * <h3>Campaign Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CampaignDao extends PagingAndSortingRepository<Campaign, String> {

	/**
	 * 
	 * @param clientId
	 * @param pageable
	 * @return	returns Campaigns
	 */
	Page<Campaign> findByClientId(String clientId, Pageable pageable);

	/**
	 * 
	 * @param createdBy
	 * @param pageable
	 * @return	returns Campaigns
	 */
	Page<Campaign> findByCreatedBy(String createdBy, Pageable pageable);
	
	/**
	 * 
	 * @param title
	 * @param clientId
	 * @return		returns Campaign
	 */
	Campaign findByTitleAndClientId(String title, String clientId);

	/**
	 * 
	 * @param startTime
	 * @param endTime
	 * @return		returns Campaigns
	 */
	List<Campaign> findByEndDateGreaterThanEqualAndStartDateLessThan(long startTime, long endTime);
	
	/**
	 * 
	 * @param team
	 * @return	returns Campaigns
	 */
	List<Campaign> findByTeam(Team team);
}
