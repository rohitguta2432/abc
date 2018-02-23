package com.socioseer.integration.service.impl.twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socioseer.common.domain.model.CampaignHashTagCount;
import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;
import com.socioseer.integration.dao.TwitterDao;
import com.socioseer.integration.service.api.SocialPostAggregator;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Service("twitterHashTagAggregator")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TwitterHashTagAggregator implements SocialPostAggregator<List<CampaignHashTagSummary>> {

  private final TwitterDao twitterDao;

  /**
   * @param	lastCapturedAt long type data
   * @return	returns CampaignHashTagSummary list
   */
  @Override
  public List<CampaignHashTagSummary> aggregateData(long lastCapturedAt) {
    List<CampaignHashTagCount> campaignHashTagCountList =
        twitterDao.findHashTagCount(lastCapturedAt);
    if (CollectionUtils.isEmpty(campaignHashTagCountList)) {
      return Collections.emptyList();
    }

    Map<String, CampaignHashTagSummary> campaignHashTagSummaryMap =
        new HashMap<String, CampaignHashTagSummary>();
    CampaignHashTagSummary campaignHashTagSummary = null;
    Map<String, Integer> hashTagCount = null;
    for (CampaignHashTagCount campaignHashTagCount : campaignHashTagCountList) {
      campaignHashTagSummary = campaignHashTagSummaryMap.get(campaignHashTagCount.getCampaignId());
      if (campaignHashTagSummary == null) {
        campaignHashTagSummary = new CampaignHashTagSummary();
        campaignHashTagSummary.setCampaignId(campaignHashTagCount.getCampaignId());
        campaignHashTagSummary.setClientId(campaignHashTagCount.getClientId());
        hashTagCount = new HashMap<String, Integer>();
      } else {
        hashTagCount = campaignHashTagSummary.getHashTagCount();
      }
      hashTagCount.put(campaignHashTagCount.getHashTag(), campaignHashTagCount.getCount());
      campaignHashTagSummary.setHashTagCount(hashTagCount);
      campaignHashTagSummaryMap.put(campaignHashTagCount.getCampaignId(), campaignHashTagSummary);
    }
    return new ArrayList<CampaignHashTagSummary>(campaignHashTagSummaryMap.values());
  }

}
