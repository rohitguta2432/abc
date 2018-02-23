package com.socioseer.integration.dao;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;

@Repository
public class FacebookDao {

  private static final String COLLECTION_FACEBOOK = "facebook";

  @Autowired
  private MongoTemplate mongoTemplate;
  
  public List<CampaignSummary> findTotalFollowersPerPage(long capturedAt) {
    Aggregation aggregation =
        newAggregation(
            match(Criteria.where("capturedAt").gte(capturedAt).and("POST_TYPE").is("POST")),
            Aggregation.sort(new Sort(Direction.DESC, "capturedAt")),
            group("id_str").max("favorite_count").as("max").first("id_str").as("socialPostId"));
    final AggregationResults<CampaignSummary> aggregationResult =
        mongoTemplate.aggregate(aggregation, COLLECTION_FACEBOOK, CampaignSummary.class);
    return aggregationResult.getMappedResults();
  }

  public List<CampaignSummary> findCommentsPerPost(long capturedAt) {
    Aggregation aggregation =
        newAggregation(
            match(Criteria.where("capturedAt").gte(capturedAt).and("POST_TYPE").is("POST")),
            Aggregation.sort(new Sort(Direction.DESC, "capturedAt")),
            group("id").first("clientId").as("clientId").first("handlerId").as("handlerId").first("comments").as("comments").first("id").as("id"),
            Aggregation.unwind("comments.data"),
            group("id").count().as("commentCount").first("clientId").as("clientId").first("handlerId").as("handlerId").first("id").as("socialPostId")
            );
    final AggregationResults<CampaignSummary> aggregationResult =
        mongoTemplate.aggregate(aggregation, COLLECTION_FACEBOOK, CampaignSummary.class);
    return aggregationResult.getMappedResults();
  }

  public List<CampaignSummary> findSharesPerPost(long capturedAt) {
    Aggregation aggregation =
        newAggregation(
            match(Criteria.where("capturedAt").gte(capturedAt).and("POST_TYPE").is("POST").and("shares").ne(null)),
            Aggregation.sort(new Sort(Direction.DESC, "capturedAt")),
            group("id").first("clientId").as("clientId").first("handlerId").as("handlerId").first("shares.count").as("retweetCount").first("id").as("socialPostId"));
    final AggregationResults<CampaignSummary> aggregationResult =
        mongoTemplate.aggregate(aggregation, COLLECTION_FACEBOOK, CampaignSummary.class);
    return aggregationResult.getMappedResults();
  }

  public List<CampaignSummary> findLikesPerPost(long capturedAt) {
    Aggregation aggregation =
        newAggregation(
            match(Criteria.where("capturedAt").gte(capturedAt).and("POST_TYPE").is("POST")),
            Aggregation.sort(new Sort(Direction.DESC, "capturedAt")),
            group("id").first("clientId").as("clientId").first("handlerId").as("handlerId").first("likes").as("likes").first("id").as("id"),
            Aggregation.unwind("likes.data"),
            group("id").count().as("likeCount").first("clientId").as("clientId").first("handlerId").as("handlerId").first("id").as("socialPostId")
            );
    final AggregationResults<CampaignSummary> aggregationResult =
        mongoTemplate.aggregate(aggregation, COLLECTION_FACEBOOK, CampaignSummary.class);
    return aggregationResult.getMappedResults();
  }
  
  
}
