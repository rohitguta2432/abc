package com.socioseer.common.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@JsonInclude(value = Include.NON_NULL)
public class PlatformReportDto {
  private int postCount;
  private String platform;
  private long likeCount;
  private long shareCount;
  private long commentCount;
}
