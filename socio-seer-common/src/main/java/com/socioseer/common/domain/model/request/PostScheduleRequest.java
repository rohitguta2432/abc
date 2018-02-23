package com.socioseer.common.domain.model.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@AllArgsConstructor
public class PostScheduleRequest {
  
  private String clientId;
  private String clientName;
  private String platformId;
  private String campaignId;
  private String campaignName;
  private int frequencyCode;
  private List<ScheduleTime> scheduleTime;
  private String createdBy;
  private String postId;
  
}