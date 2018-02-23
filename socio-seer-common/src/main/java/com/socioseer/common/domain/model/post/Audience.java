package com.socioseer.common.domain.model.post;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@Document(collection = "audience")
@EqualsAndHashCode(callSuper = false)
public class Audience {

  @Id
  private String id;
  private String platformId;
  private String ownerName;
  private String description;
  private String location;
}
