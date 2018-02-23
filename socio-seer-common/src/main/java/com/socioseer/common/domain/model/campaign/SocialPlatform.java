package com.socioseer.common.domain.model.campaign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.socioseer.common.domain.model.AbstractEntity;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Document(collection = "social_platform")
public class SocialPlatform extends AbstractEntity {
  
  private static final long serialVersionUID = -3763967311783865022L;

  @Id
  private String id;
  private String name;
}
