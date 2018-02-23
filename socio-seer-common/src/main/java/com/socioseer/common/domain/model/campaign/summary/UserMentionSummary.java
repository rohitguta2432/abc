package com.socioseer.common.domain.model.campaign.summary;


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
@Document(collection = "user_mention_summary")
@JsonInclude(content = Include.NON_EMPTY)
public class UserMentionSummary extends AbstractEntity {

  private static final long serialVersionUID = -7801895116350879333L;

  @Id
  private String id;
  private String clientId;
  private String handlerId;
  private String platform;
  private int mentionCount;
  private long createdTime;

}
