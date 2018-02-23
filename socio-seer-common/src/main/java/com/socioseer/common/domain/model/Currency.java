package com.socioseer.common.domain.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = { "id",  "name" }, callSuper = false)
@Document(collection = "currency")
@JsonInclude(content = Include.NON_NULL)
public class Currency extends AbstractEntity {
  
  @Id
  private String id;
  private String name;
  private String symbol;
  private int status;
}
