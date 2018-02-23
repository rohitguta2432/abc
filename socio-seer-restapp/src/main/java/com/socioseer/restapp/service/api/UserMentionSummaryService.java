package com.socioseer.restapp.service.api;

import java.util.List;

import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.campaign.summary.UserMentionSummary;
/**
 * <h3>UserMentionSummary Services</h3>
 * @author OrangeMantra
 * @since  JDK 1.8
 * @version 1.0
 *
 */
public interface UserMentionSummaryService extends CrudApi<UserMentionSummary> {

	/**
	 * <b>Aggregate UserMentionSummary by clientId</b>
	 * @param clientId
	 * @return returns List of UserMentionSummary
	 */
  List<UserMentionSummary> aggregateByClientId(String clientId);

  List<UserMentionSummary> aggregateByClientIdCampaign(String clientId, Campaign campaign);

}
