package com.socioseer.common.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
public class CampaignHashTagCount {
  private String campaignId;
  private String hashTag;
  private int count;
  private String clientId;
}
