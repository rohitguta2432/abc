package com.socioseer.common.domain.model.campaign.summary;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
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
@EqualsAndHashCode(of = {"id", "clientId"}, callSuper = false)
@NoArgsConstructor
@Document(collection = "campaign_hash_tag_summary")
@JsonInclude(content = Include.NON_EMPTY)
public class CampaignHashTagSummary extends AbstractEntity {

  private static final long serialVersionUID = -1422392941919132682L;

  @Id
  private String id;
  private String campaignId;
  private String clientId;
  private Map<String, Integer> hashTagCount;
}
