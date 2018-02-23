package com.socioseer.common.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
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
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Document(collection = "forgot-password")
@JsonInclude(content = Include.NON_NULL)
public class ForgotPassword extends AbstractEntity {

  private static final long serialVersionUID = 505465917617036444L;

  @Id
  private String id;
  private String userId;
  private String token;
  private long generatedAt;

}
