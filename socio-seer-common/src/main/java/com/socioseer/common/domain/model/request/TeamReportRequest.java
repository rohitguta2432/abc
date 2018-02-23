package com.socioseer.common.domain.model.request;

import java.util.List;

import lombok.Data;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
public class TeamReportRequest {
  private String clientId; 
  private String authorId;
  private List<String> platformIds;
  private String startDate;
  private String endDate;
}
