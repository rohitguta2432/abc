package com.socioseer.common.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotification {

  @NonNull
  private String from;
  @NonNull
  private List<String> toList;
  private List<String> ccList;
  @NonNull
  private String subject;
  @NonNull
  private String message;

}
