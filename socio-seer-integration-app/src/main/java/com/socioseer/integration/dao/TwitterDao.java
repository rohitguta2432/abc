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
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.model.CampaignHashTagCount;
import com.socioseer.common.domain.model.TwitterStat;
import com.socioseer.common.domain.model.campaign.summary.UserMentionSummary;

@Repository
public class TwitterDao {

  private static final String COLLECTION_TWITTER = "twitter";
  private static final String TWITTER_COMMENT_COLLECTION = "twitter-comment";

  @Autowired
  private MongoTemplate mongoTemplate;

  /*
   * public List<TwitterStat> findTotalCommentsPerPost(long capturedAt) { Aggregation aggregation =
   * newAggregation(
   * match(Criteria.where("in_reply_to_status_id").ne(null).and("capturedAt").gte(capturedAt)),
   * group("in_reply_to_status_id").count().as("count").first("in_reply_to_status_id")
   * .as("socialPostId")); final AggregationResults<TwitterStat> aggregationResult =
   * mongoTemplate.aggregate(aggregation, COLLECTION_TWITTER, TwitterStat.class); return
   * aggregationResult.getMappedResults(); }
   */


  public List<TwitterStat> findTotalCommentsPerPost(long capturedAt) {
    Aggregation aggregation = newAggregation(match(Criteria.where("capturedAt").gte(capturedAt)),
        group("in_reply_to_status_id").count().as("count").first("in_reply_to_status_id")
            .as("socialPostId"));
    final AggregationResults<TwitterStat> aggregationResult =
        mongoTemplate.aggregate(aggregation, TWITTER_COMMENT_COLLECTION, TwitterStat.class);
    return aggregationResult.getMappedResults();
  }


  public List<TwitterStat> findMaximumLikesPerPost(long capturedAt) {
    Aggregation aggregation = newAggregation(match(Criteria.where("capturedAt").gte(capturedAt)),
        Aggregation.sort(new Sort(Direction.DESC, "favorite_count")),
        group("id_str").max("favorite_count").as("max").first("id_str").as("socialPostId"));
    final AggregationResults<TwitterStat> aggregationResult = mongoTemplate.aggregate(aggregation,
        PlatformConstant.TWITTER_POST_COLLECTION, TwitterStat.class);
    return aggregationResult.getMappedResults();
  }

  public List<TwitterStat> findMaximumRetweetPerPost(long capturedAt) {
    Aggregation aggregation = newAggregation(
        match(Criteria.where("retweeted").is(true).and("capturedAt").gte(capturedAt)),
        Aggregation.sort(new Sort(Direction.DESC, "retweet_count")),
        group("id_str").max("retweet_count").as("max").first("id_str").as("socialPostId"));
    final AggregationResults<TwitterStat> aggregationResult = mongoTemplate.aggregate(aggregation,
        PlatformConstant.TWITTER_POST_COLLECTION, TwitterStat.class);
    return aggregationResult.getMappedResults();
  }

  public List<TwitterStat> findTotalFollowersPerHandlerId(long capturedAt) {
    Aggregation aggregation = newAggregation(
        match(Criteria.where("in_reply_to_status_id").is(null).and("capturedAt").gte(capturedAt)),
        Aggregation.sort(new Sort(Direction.DESC, "user.followers_count")),
        group("user.id_str").max("user.followers_count").as("max").first("user.id_str")
            .as("handlerId").first("id_str").as("socialPostId"));
    final AggregationResults<TwitterStat> aggregationResult =
        mongoTemplate.aggregate(aggregation, COLLECTION_TWITTER, TwitterStat.class);
    return aggregationResult.getMappedResults();
  }

  public List<UserMentionSummary> findUserMentionSummary(long capturedAt) {
    Aggregation aggregation = newAggregation(
        match(Criteria.where("tweetType").is(ModelConstants.MENTION_TIMELINE).and("capturedAt")
            .gte(capturedAt)),
        group(Fields.fields("clientId").and("handlerId")).count().as("mentionCount")
            .first("clientId").as("clientId").first("handlerId").as("handlerId"));
    final AggregationResults<UserMentionSummary> aggregationResult =
        mongoTemplate.aggregate(aggregation, PlatformConstant.TWITTER, UserMentionSummary.class);
    return aggregationResult.getMappedResults();
  }

  public List<CampaignHashTagCount> findHashTagCount(long capturedAt) {
    Aggregation aggregation = newAggregation(
        match(Criteria.where("campaignId").ne(null).and("capturedAt").gte(capturedAt)),
        Aggregation.unwind("postHashTags"),
        group("campaignId", "postHashTags").count().as("count").first("postHashTags").as("hashTag")
            .first("campaignId").as("campaignId").first("clientId").as("clientId"));
    final AggregationResults<CampaignHashTagCount> aggregationResult = mongoTemplate.aggregate(
        aggregation, PlatformConstant.TWITTER_HASHTAG_COLLECTION, CampaignHashTagCount.class);
    return aggregationResult.getMappedResults();
  }

}
