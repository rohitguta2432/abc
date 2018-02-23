package com.socioseer.common.domain.model.post;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.AbstractEntity;
import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.request.PostScheduleRequest;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@Document(collection = "post")
@EqualsAndHashCode(callSuper = false)
public class Post extends AbstractEntity implements Serializable {

  private static final long serialVersionUID = -7517992662679183483L;
  public static final String POST_HASH_TAG_KEY = "postHashTags";

  @Id
  private String id;
  private String clientId;
  private String campaignId;
  private String campaignTitle;
  private String url;
  private String text;
  private List<String> mediaIds;
  private String brandId;
  private String brandName;

  @DBRef
  private List<SocialHandler> socialHandlers;
  private boolean isNotifyUser;
  private List<String> audiences;
  private int status;
  private String approvedBy;

  private List<PostScheduleRequest> postScheduleRequests;

  private Map<String, List<String>> selectedHandlers;

  @Transient
  private Boolean isDraft;
 
  private String createdName;
  @Transient
  private List<Date> runat;

  @Transient
  private Boolean isPublished;

  @Transient
  private String taskId;

  @Transient
  private List<Media> mediaUrls;
  
  @Transient
  private List<String> urls;
  
  @Indexed
  private List<String> hashTags;

  @Transient
  private List<String> audiencesName;
  
  @Transient
  private Map<String ,String> socialHandlerName;

}
