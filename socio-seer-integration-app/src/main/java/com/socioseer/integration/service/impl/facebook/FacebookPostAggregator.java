package com.socioseer.integration.service.impl.facebook;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;
import com.socioseer.integration.dao.FacebookDao;
import com.socioseer.integration.service.api.SocialPostAggregator;

/**
 * <h3>FacebookPost Aggregator</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service("facebookPostAggregator")
public class FacebookPostAggregator implements SocialPostAggregator<List<CampaignSummary>> {

  @Autowired
  private FacebookDao facebookDao;

  /**
   * <b>Aggregate Datat</b>
   * @param	lastCapturedAt
   * @return returns CampaignSummary list
   */
  @Override
  public List<CampaignSummary> aggregateData(long lastCapturedAt) {
    Map<String, CampaignSummary> result = new HashMap<String, CampaignSummary>();
    aggregateLikes(lastCapturedAt, result);
    aggregateShares(lastCapturedAt, result);
    aggregateComments(lastCapturedAt, result);
    //aggregateFollowersCount(lastCapturedAt, result);
    /*aggregateCampaignHashTags(lastCapturedAt);*/
    if (CollectionUtils.isEmpty(result)) {
      return Collections.emptyList();
    }
    return Lists.newArrayList(result.values());
  }

  /*private void aggregateCampaignHashTags(long lastCapturedAt) {
    List<CampaignHashTagCount> campaignHashTagCountList =
        facebookDao.findHashTagCount(lastCapturedAt);
  }*/

  /**
   * <b>Aggregate Followers Count</b>
   * @param lastCapturedAt
   * @param result
   */
  private void aggregateFollowersCount(long lastCapturedAt, Map<String, CampaignSummary> result) {
    List<CampaignSummary> followersPerHandlerIds =
        facebookDao.findTotalFollowersPerPage(lastCapturedAt);
    if (CollectionUtils.isEmpty(followersPerHandlerIds)) {
      return;
    }
    followersPerHandlerIds.forEach(cs -> {
      String socialPostId = cs.getSocialPostId();
      CampaignSummary campaignSummary = result.get(socialPostId);
      if (campaignSummary == null) {
        campaignSummary = new CampaignSummary();
        campaignSummary.setSocialPostId(socialPostId);
      }
      campaignSummary.setHandlerId(cs.getHandlerId());
      campaignSummary.setFollowersCount(cs.getFollowersCount());
      result.put(socialPostId, campaignSummary);
    });
  }

  /**
   * <b>Aggregate Comments</b>
   * @param lastCapturedAt
   * @param result
   */
  private void aggregateComments(long lastCapturedAt, Map<String, CampaignSummary> result) {
    List<CampaignSummary> totalCommentPerPost = facebookDao.findCommentsPerPost(lastCapturedAt);
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
      campaignSummary.setCommentCount(ts.getCommentCount());
      result.put(socialPostId, campaignSummary);
    });
  }

  /**
   * <b>Aggregate Shares</b>
   * @param lastCapturedAt
   * @param result
   */
  private void aggregateShares(long lastCapturedAt, Map<String, CampaignSummary> result) {
    List<CampaignSummary> maxTweetPerPost = facebookDao.findSharesPerPost(lastCapturedAt);
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
      campaignSummary.setRetweetCount(ts.getRetweetCount());
      result.put(socialPostId, campaignSummary);
    });
  }

  /**
   * <b>Aggregate Likes</b>
   * @param lastCapturedAt
   * @param result
   */
  private void aggregateLikes(long lastCapturedAt, Map<String, CampaignSummary> result) {
    List<CampaignSummary> maxLikesPerPost = facebookDao.findLikesPerPost(lastCapturedAt);
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
      campaignSummary.setLikeCount(ts.getLikeCount());
      result.put(socialPostId, campaignSummary);
    });
  }

}
