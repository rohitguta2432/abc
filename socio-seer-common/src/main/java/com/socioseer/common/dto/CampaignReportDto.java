package com.socioseer.common.dto;

import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.socioseer.common.domain.model.campaign.summary.UserMentionSummary;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@JsonInclude(value = Include.NON_NULL)
public class CampaignReportDto {
  private String campaignId;
  private String campaignName;
  private TopPostDto topPost;
  private String ProfileImageName;
  private List<PlatformReportDto> platformReports;
  private List<String> platformNames;
  private long startDate;
  private long endDate;
  private List<UserMentionSummary> userMentionSummarieList;
}
