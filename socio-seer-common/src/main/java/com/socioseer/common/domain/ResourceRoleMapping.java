package com.socioseer.common.domain;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.socioseer.common.domain.model.AbstractEntity;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"resourceName", "url"}, callSuper = false)
@Document(collection = "resourceRoleMapping")
@JsonInclude(content = Include.NON_EMPTY)
public class ResourceRoleMapping extends AbstractEntity {

  @Id
  private String id;
  private String resourceName;
  private String url;
  @Transient
  private List<String> roleNames;
  private List<Role> roles;

}
