package com.socioseer.common.domain.model.post;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.socioseer.common.domain.model.AbstractEntity;
import com.socioseer.common.domain.model.campaign.SocialPlatform;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder(builderMethodName = "postScheduleBuilder")
@Document(collection = "post_schedule")

public class PostSchedule extends AbstractEntity implements Serializable {
  public static final String OBJECT_KEY = "POST_SCHEDULE";
  private static final long serialVersionUID = 1L;
  @Id
  private String id;
  private String clientId;
  private String clientName;
  private String campaignId;
  private String campaignName;
  @DBRef
  private Post post;
  @DBRef
  private SocialPlatform platform;
  private List<PostHandler> postHandlers;
  @Indexed
  private Date runAt;
  private boolean isActive;
  private boolean isExecuted;
  
  public PostSchedule(Post post, SocialPlatform platform, List<PostHandler> postHandlers,
      Date runAt, boolean isActive, boolean isExecuted, long createdDate, String createdBy ,String clientId ,String clientName ,String campaignId ,String campaignName ) {
    super(createdDate, createdBy, null, null);
    this.post = post;
    this.postHandlers = postHandlers;
    this.runAt = runAt;
    this.isActive = isActive;
    this.platform = platform;
    this.isExecuted = isExecuted;
    this.clientId =clientId;
    this.clientName =clientName;
    this.campaignId =campaignId;
    this.campaignName =campaignName;
  }

  public static PostScheduleBuilder builder(Post post) {
    return postScheduleBuilder().post(post);
  }
  
}
