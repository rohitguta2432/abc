package com.socioseer.restapp.service.api;

import java.util.List;

import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;

/**
 * <h3>CampaignHashTagSummary Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CampaignHashTagSummaryService extends CrudApi<CampaignHashTagSummary> {

	/**
	   * <b>Get campaignHashTagSummary list by clientId</b>
	   * @param		clientId
	   * @return	returns CampaignHashTagSummary list
	   */
  List<CampaignHashTagSummary> findByClientId(String clientId);
}
