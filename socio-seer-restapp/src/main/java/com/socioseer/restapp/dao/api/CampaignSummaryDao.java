package com.socioseer.restapp.dao.api;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;

/**
 * <h3>CampaignSummary Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CampaignSummaryDao extends PagingAndSortingRepository<CampaignSummary, String> {
	
	/**
	 * 
	 * @param postId
	 * @return 	returns CampaignSummary    
	 */
	CampaignSummary getByPostId(String postId);
}
