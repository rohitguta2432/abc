package com.socioseer.common.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
public class CampaignSummaryDetailDto {

  @NonNull
  private String campaignId;
  @NonNull
  private String clientId;
  private List<PlatformReportDto> platformReports;
  private List<PostReportDto> postReportDtos;

}
