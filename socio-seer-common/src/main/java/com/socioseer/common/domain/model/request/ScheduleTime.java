package com.socioseer.common.domain.model.request;

import lombok.Data;
import lombok.ToString;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@ToString
public class ScheduleTime {
  private String startTime;
  private String endTime;
}
