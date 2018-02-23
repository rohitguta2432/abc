package com.socioseer.common.domain.model;


import java.util.List;

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
public class CampaignSchedulerDto {

  private String id;
  private String name;
  private String description;
  private List<String> socialPlatformNames;
}
