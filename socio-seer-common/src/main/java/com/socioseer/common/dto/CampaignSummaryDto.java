package com.socioseer.common.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.socioseer.common.domain.model.ClientFollowersInfo;
import com.socioseer.common.domain.model.campaign.summary.UserMentionSummary;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class CampaignSummaryDto {

  private List<String> hashTags;
  @NonNull
  private String clientId;
  @NonNull
  private List<CampaignReportDto> campaignReports;
  private List<ClientFollowersInfo> clientFollowersInfoList;
  private List<UserMentionSummary> userMentionSummarieList;
  private List<String> platformName;

}
