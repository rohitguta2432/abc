package com.socioseer.common.dto;

import java.io.Serializable;

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
public class PostScheduleDto implements Serializable{
  private String id;
  private PostDto postDto;
  private String platform;
  private String handlerId;
}
