package com.socioseer.common.dto;

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
public class PostReportDto {

  @NonNull
  private String postId;
  @NonNull
  private String description;
  @NonNull
  private String platform;
  private long likeCount;
  private long commentCount;
  private long shareCount;
  private String mediaUrl;
}
