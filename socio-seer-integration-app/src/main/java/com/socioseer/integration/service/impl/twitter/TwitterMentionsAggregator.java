package com.socioseer.integration.service.impl.twitter;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.domain.model.campaign.summary.UserMentionSummary;
import com.socioseer.integration.dao.TwitterDao;
import com.socioseer.integration.service.api.SocialPostAggregator;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TwitterMentionsAggregator implements SocialPostAggregator<List<UserMentionSummary>> {

  private final TwitterDao twitterDao;

  /**
   * @param lastCapturedAt	long type
   * @return	returns UserMentionSummary list
   */
  @Override
  public List<UserMentionSummary> aggregateData(long lastCapturedAt) {

    List<UserMentionSummary> userMentionSummaries =
        twitterDao.findUserMentionSummary(lastCapturedAt);
    userMentionSummaries.forEach(userMentionSummary -> {
      userMentionSummary.setPlatform(PlatformConstant.TWITTER);
    });
    return userMentionSummaries;
  }
}
