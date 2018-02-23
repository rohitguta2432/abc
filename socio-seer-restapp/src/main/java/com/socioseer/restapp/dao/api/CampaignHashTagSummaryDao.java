package com.socioseer.restapp.dao.api;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;

/**
 * <h3>CampaignHashTagSummary Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CampaignHashTagSummaryDao extends
    PagingAndSortingRepository<CampaignHashTagSummary, String> {

}
