package com.socioseer.restapp.service.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;

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

import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.campaign.summary.UserMentionSummary;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.UserMentionSummaryDao;
import com.socioseer.restapp.service.api.UserMentionSummaryService;
import com.socioseer.restapp.service.util.DateUtil;

/**
 * <h3>UserMentionSummary Service Implementation</h3>
 * @author OrangeMantra
 * @since  JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMentionSummaryServiceImpl implements UserMentionSummaryService {


  private final UserMentionSummaryDao userMentionSummaryDao;
  private final MongoTemplate mongoTemplate;

  /**
   * <b>Save UserMentionSummary</b>
   * @param userMentionSummary
   * @return returns UserMentionSummary
   */
  @Override
  public UserMentionSummary save(UserMentionSummary userMentionSummary) {

    userMentionSummary.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
    try {
      return userMentionSummaryDao.save(userMentionSummary);
    } catch (Exception e) {
      String message =
          String.format(
              "Error while saving user mention data for client id : %s and handler id : %s",
              userMentionSummary.getClientId(), userMentionSummary.getHandlerId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Aggregate UserMentionSummary by clientId</b>
   */
  @Override
  public List<UserMentionSummary> aggregateByClientIdCampaign(String clientId, Campaign campaign) {
    Aggregation aggregation =
        newAggregation(
        	Aggregation.match(Criteria.where("createdDate").lte(campaign.getEndDate())),
        	Aggregation.match(Criteria.where("clientId").is(clientId)),
            Aggregation.sort(new Sort(Direction.DESC, "createdDate")),
            Aggregation.group("clientId", "platform", "handlerId").first("clientId").as("clientId")
                .first("platform").as("platform").first("handlerId").as("handlerId")
                .first("mentionCount").as("mentionCount"),
            Aggregation.group("clientId", "platform").sum("mentionCount").as("mentionCount")
                .first("clientId").as("clientId").first("platform").as("platform"));
    final AggregationResults<UserMentionSummary> aggregationResult =
        mongoTemplate.aggregate(aggregation, UserMentionSummary.class, UserMentionSummary.class);
    return aggregationResult.getMappedResults();

  }
  
  /**
   * <b>Aggregate UserMentionSummary by clientId</b>
   */
  @Override
  public List<UserMentionSummary> aggregateByClientId(String clientId) {
    Aggregation aggregation =
        newAggregation(
            Aggregation.match(Criteria.where("clientId").is(clientId)),
            Aggregation.sort(new Sort(Direction.DESC, "createdDate")),
            Aggregation.group("clientId", "platform", "handlerId").first("clientId").as("clientId")
                .first("platform").as("platform").first("handlerId").as("handlerId")
                .first("mentionCount").as("mentionCount"),
            Aggregation.group("clientId", "platform").sum("mentionCount").as("mentionCount")
                .first("clientId").as("clientId").first("platform").as("platform"));
    final AggregationResults<UserMentionSummary> aggregationResult =
        mongoTemplate.aggregate(aggregation, UserMentionSummary.class, UserMentionSummary.class);
    return aggregationResult.getMappedResults();

  }

  @Override
  public UserMentionSummary update(String id, UserMentionSummary entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public UserMentionSummary get(String id) {
    // TODO Auto-generated method stub
    return null;
  }

}
