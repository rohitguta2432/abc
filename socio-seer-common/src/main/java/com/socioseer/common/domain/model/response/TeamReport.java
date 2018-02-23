package com.socioseer.common.domain.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamReport {
  private String name;
  private int postCount;
  private int approvedCount;
  private int pendingCount;
  private int rejectedCount;
  private int pubishedCount;
}
