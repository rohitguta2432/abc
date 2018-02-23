package com.socioseer.common.dto;

import java.io.Serializable;
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
public class PostDto implements Serializable {
  private String id;
  private String clientId;
  private String campaignId;
  private String url;
  private String text;
  private List<MediaDto> mediaList;
}
