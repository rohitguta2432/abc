package com.socioseer.common.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(of = {"id", "userId"}, callSuper = false)
@NoArgsConstructor
@Document(collection = "notification")
@JsonInclude(content = Include.NON_NULL)
public class Alert extends AbstractEntity {

  @Id
  private String id;
  @Indexed()
  private String userId;
  private String postId;
  private String notificationType;
  private String description;
  private String message;
  private int status;

}
