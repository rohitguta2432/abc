package com.socioseer.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class FBPostDto {

  public static final String CACHE_OBJECT = "FB_POST_DTO";

  @NonNull
  private String postId;
  @NonNull
  private String socialPostId;
  private String handlerId;
  @NonNull
  private String postScheduleId;
  @NonNull
  private String campaignId;


}
