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
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto implements Serializable {
  private String id;
  private String originalFileName;
  private String hashFileName;
  private String clientId;
  private String mediaType;
  private String fileLocation;
  private String videoURL;
  private String mediaFileURL;
}
