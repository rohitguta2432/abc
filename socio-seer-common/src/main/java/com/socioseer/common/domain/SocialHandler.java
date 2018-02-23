package com.socioseer.common.domain;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.socioseer.common.domain.model.AbstractEntity;
import com.socioseer.common.domain.model.campaign.SocialPlatform;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@EqualsAndHashCode(of = {"id", "clientId"}, callSuper = false)
@Document(collection = "social_handler")
@JsonInclude(content = Include.NON_NULL)
public class SocialHandler extends AbstractEntity {

  public static final String OBJECT_KEY = "social_handler";
  private static final long serialVersionUID = -5772769246018781550L;

  @Id
  private String id;
  private String clientId;
  private String brandId;
  private String brandName;
  private int status;
  private String handler;
  private String title;

  @DBRef
  private SocialPlatform socialPlatform;
  private Map<String, String> accessToken;

  @Transient
  private String platformId;
}
