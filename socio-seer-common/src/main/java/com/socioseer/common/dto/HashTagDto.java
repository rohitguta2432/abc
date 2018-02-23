package com.socioseer.common.dto;

import java.io.Serializable;
import java.util.List;

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
@NoArgsConstructor
@RequiredArgsConstructor
public class HashTagDto implements Serializable {

  private static final long serialVersionUID = -7136177805359147683L;

  @NonNull
  private String postId;
  @NonNull
  private List<String> hashTags;
  @NonNull
  private String clientId;
  private String campaignId;
  private String maxId;
  private int count;


}
