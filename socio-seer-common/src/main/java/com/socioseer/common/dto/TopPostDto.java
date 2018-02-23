package com.socioseer.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.socioseer.common.domain.model.post.Post;

import lombok.Data;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
public class TopPostDto {
  private String platform;
  @JsonIgnore
  private String postId;
  private Post post;
  private long likeCount;
}
