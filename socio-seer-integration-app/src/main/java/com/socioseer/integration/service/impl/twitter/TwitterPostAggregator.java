package com.socioseer.integration.service.impl.twitter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.socioseer.common.domain.model.CampaignHashTagCount;
import com.socioseer.common.domain.model.TwitterStat;
import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;
import com.socioseer.integration.dao.TwitterDao;
import com.socioseer.integration.service.api.SocialPostAggregator;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Service("twitterPostAggregator")
public class TwitterPostAggregator implements SocialPostAggregator<List<CampaignSummary>> {

  @Autowired
  private TwitterDao twitterDao;

  /**
   * @param		lastCapturedAt		long type
   * @return	returns CampaignSummary list
   */
  @Override
  public List<CampaignSummary> aggregateData(long lastCapturedAt) {
    Map<String, CampaignSummary> result = new HashMap<String, CampaignSummary>();
    aggregateMaxLikes(lastCapturedAt, result);
    aggregateMaxRetweet(lastCapturedAt, result);
    aggregateTotalComments(lastCapturedAt, result);
    aggregateFollowersCount(lastCapturedAt, result);
    aggregateCampaignHashTags(lastCapturedAt);
    if (CollectionUtils.isEmpty(result)) {
      return Collections.emptyList();
    }
    return Lists.newArrayList(result.values());
  }

  /**
   * 
   * @param lastCapturedAt
   */
  private void aggregateCampaignHashTags(long lastCapturedAt) {
    List<CampaignHashTagCount> campaignHashTagCountList =
        twitterDao.findHashTagCount(lastCapturedAt);
  }

  /**
   * 
   * @param lastCapturedAt
   * @param result
   */
  private void aggregateFollowersCount(long lastCapturedAt, Map<String, CampaignSummary> result) {
    List<TwitterStat> followersPerHandlerIds =
        twitterDao.findTotalFollowersPerHandlerId(lastCapturedAt);
    if (CollectionUtils.isEmpty(followersPerHandlerIds)) {
      return;
    }
    followersPerHandlerIds.forEach(ts -> {
      String socialPostId = ts.getSocialPostId();
      CampaignSummary campaignSummary = result.get(socialPostId);
      if (campaignSummary == null) {
        campaignSummary = new CampaignSummary();
        campaignSummary.setSocialPostId(socialPostId);
      }
      campaignSummary.setHandlerId(ts.getHandlerId());
      campaignSummary.setFollowersCount(ts.getCount());
      result.put(socialPostId, campaignSummary);
    });
  }

  /**
   * 
   * @param lastCapturedAt
   * @param result
   */
  private void aggregateTotalComments(long lastCapturedAt, Map<String, CampaignSummary> result) {
    List<TwitterStat> totalCommentPerPost = twitterDao.findTotalCommentsPerPost(lastCapturedAt);
    if (CollectionUtils.isEmpty(totalCommentPerPost)) {
      return;
    }
    totalCommentPerPost.forEach(ts -> {
      String socialPostId = ts.getSocialPostId();
      CampaignSummary campaignSummary = result.get(socialPostId);
      if (campaignSummary == null) {
        campaignSummary = new CampaignSummary();
        campaignSummary.setSocialPostId(socialPostId);
      }
      campaignSummary.setCommentCount(ts.getCount());
      result.put(socialPostId, campaignSummary);
    });
  }

  /**
   * 
   * @param lastCapturedAt
   * @param result
   */
  private void aggregateMaxRetweet(long lastCapturedAt, Map<String, CampaignSummary> result) {
    List<TwitterStat> maxTweetPerPost = twitterDao.findMaximumRetweetPerPost(lastCapturedAt);
    if (CollectionUtils.isEmpty(maxTweetPerPost)) {
      return;
    }
    maxTweetPerPost.forEach(ts -> {
      String socialPostId = ts.getSocialPostId();
      CampaignSummary campaignSummary = result.get(socialPostId);
      if (campaignSummary == null) {
        campaignSummary = new CampaignSummary();
        campaignSummary.setSocialPostId(socialPostId);
      }
      campaignSummary.setRetweetCount(ts.getMax());
      result.put(socialPostId, campaignSummary);
    });
  }

  /**
   * 
   * @param lastCapturedAt
   * @param result
   */
  private void aggregateMaxLikes(long lastCapturedAt, Map<String, CampaignSummary> result) {
    List<TwitterStat> maxLikesPerPost = twitterDao.findMaximumLikesPerPost(lastCapturedAt);
    if (CollectionUtils.isEmpty(maxLikesPerPost)) {
      return;
    }
    maxLikesPerPost.forEach(ts -> {
      String socialPostId = ts.getSocialPostId();
      CampaignSummary campaignSummary = result.get(socialPostId);
      if (campaignSummary == null) {
        campaignSummary = new CampaignSummary();
        campaignSummary.setSocialPostId(socialPostId);
      }
      campaignSummary.setLikeCount(ts.getMax());
      result.put(socialPostId, campaignSummary);
    });
  }

}
