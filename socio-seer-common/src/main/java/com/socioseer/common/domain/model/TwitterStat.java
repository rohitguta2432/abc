package com.socioseer.common.domain.model;

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
@NoArgsConstructor
@AllArgsConstructor
public class TwitterStat {
  private String _id;
  private String socialPostId;
  private int count;
  private String handlerId;
  private int max;
}
