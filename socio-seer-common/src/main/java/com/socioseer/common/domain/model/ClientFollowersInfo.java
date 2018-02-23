package com.socioseer.common.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Document(collection = "client_followers_info")
@JsonInclude(content = Include.NON_EMPTY)
public class ClientFollowersInfo extends AbstractEntity {

  private static final long serialVersionUID = -7473568169125372864L;

  @Id
  private String id;

  @Indexed
  private String clientId;
  
  @Indexed
  private String handlerId;
  
  @Indexed
  private String platform;
  
  private long followersCount;
  
  private double followersChange;
}
