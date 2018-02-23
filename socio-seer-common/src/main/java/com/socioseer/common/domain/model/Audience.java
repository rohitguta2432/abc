package com.socioseer.common.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
@Document(collection = "audience")
@JsonInclude(content = Include.NON_NULL)
public class Audience extends AbstractEntity {

  @Id
  private String id;
  private String clientId;
  @DBRef
  private AudienceType audienceType;

}
