package com.socioseer.restapp.service.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.CampaignHashTagSummaryDao;
import com.socioseer.restapp.service.api.CampaignHashTagSummaryService;
import com.socioseer.restapp.service.util.DateUtil;

/**
 * <h3>CampaignHashTagSummary Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CampaignHashTagSummaryImpl implements CampaignHashTagSummaryService {


  private final CampaignHashTagSummaryDao campaignHashTagSummaryDao;
  private final MongoTemplate mongoTemplate;

  /**
   * <b>Save CampaignHashTagSummary</b>
   * @param		campaignHashTagSummary
   * @return	returns	CampaignHashTagSummary
   */
  @Override
  public CampaignHashTagSummary save(CampaignHashTagSummary campaignHashTagSummary) {

    try {
      campaignHashTagSummary.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      return campaignHashTagSummaryDao.save(campaignHashTagSummary);
    } catch (Exception e) {
      String message =
          String.format("Error while saving campaign hash tag summary for campaign id : %s",
              campaignHashTagSummary.getCampaignId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update CampaignHashTagSummary</b>
   * @param		id
   * @param		entity
   * @return	returns CampaignHashTagSummary
   */
  @Override
  public CampaignHashTagSummary update(String id, CampaignHashTagSummary entity) {
    return null;
  }

  /**
   * 
   * @param		id
   * @return returns CampaignHashTagSummary
   */
  @Override
  public CampaignHashTagSummary get(String id) {
    return null;
  }

  /**
   * <b>Get campaignHashTagSummary list by clientId</b>
   * @param		clientId
   * @return	returns CampaignHashTagSummary list
   */
  @Override
  public List<CampaignHashTagSummary> findByClientId(@NonNull String clientId) {
    Aggregation aggregation =
        newAggregation(
            Aggregation.match(Criteria.where("clientId").is(clientId)),
            Aggregation.sort(new Sort(Direction.DESC, "createdDate")),
            Aggregation.group("clientId", "campaignId").first("campaignId").as("campaignId")
                .first("clientId").as("clientId").first("hashTagCount").as("hashTagCount"),
            Aggregation.group("campaignId").first("campaignId").as("campaignId").first("clientId")
                .as("clientId").first("hashTagCount").as("hashTagCount"));
    final AggregationResults<CampaignHashTagSummary> aggregationResult =
        mongoTemplate.aggregate(aggregation, CampaignHashTagSummary.class,
            CampaignHashTagSummary.class);
    return aggregationResult.getMappedResults();
  }

}
