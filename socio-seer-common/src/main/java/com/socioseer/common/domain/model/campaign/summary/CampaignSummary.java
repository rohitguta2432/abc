package com.socioseer.common.domain.model.campaign.summary;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
@EqualsAndHashCode(of = {"socialPostId", "createdTime"}, callSuper = false)
@NoArgsConstructor
@Document(collection = "campaign_summary")
@JsonInclude(content = Include.NON_EMPTY)
public class CampaignSummary extends AbstractEntity {

  private static final long serialVersionUID = -8575918492197482841L;

  @Id
  private String id;
  @Indexed
  private String socialPostId;
  @Indexed
  private String campaignId;
  @Indexed
  private String postId;
  @Indexed
  private String handlerId;
  @Indexed
  private String clientId;
  @Indexed
  private String brandId;
  private long likeCount;
  private long commentCount;
  private long followersCount;
  private long retweetCount;
  private String platform;
  private long createdTime;
  
}
