package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;
import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;
import com.socioseer.common.dto.CampaignSummaryDetailDto;
import com.socioseer.common.dto.CampaignSummaryDto;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Campaign Summary services</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public interface CampaignSummaryService extends CrudApi<CampaignSummary> {


  /**
   * <b>Get CampaignSummary by clientId</b>	
   * @param clientId
   * @param filters
   * @param pageable
   * @return			returns CampaignSummaryDto
   */
  CampaignSummaryDto getCampaignSummaryByClientId(String clientId, List<Filter> filters, Pageable pageable);
  
  /**
   * <b>Get Campaign Performance by campaignId</b>
   * @param campaignId
   * @return			returns CampaignSummaryDetailDto
   */
  CampaignSummaryDetailDto getCampaignPerformance(String campaignId);

  /**
   * <b>Get CampaignSummary by postId</b>
   * @param postId
   * @return		returns CampaignSummary
   */
  CampaignSummary getCampaignSummaryByPostId(String postId);

  /**
   * <b>Get CampaignSummary by postId list</b>
   * @param postids	 list of postId	
   * @return		 returns campaignSummary list
   */
  List<CampaignSummary> getCampaignSummaryByPostIds(List<String> postids);

  /**
   * <b>Get CampaignHashTagSummary list</b>
   * @param clientId
   * @param campaignId
   * @return	returns CampaignHashTagSummary list	
   */
  List<CampaignHashTagSummary> getCampaignHashTagSummaries(String clientId, String campaignId);

}
