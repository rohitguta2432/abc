package com.socioseer.common.domain.model.campaign;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.Team;
import com.socioseer.common.domain.model.AbstractEntity;
import com.socioseer.common.domain.model.AudienceType;
import com.socioseer.common.domain.model.Brand;
import com.socioseer.common.domain.model.PostMetrics;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "campaign")
@EqualsAndHashCode(callSuper = false)
@JsonInclude(content = Include.NON_NULL)
public class Campaign extends AbstractEntity {

  private static final long serialVersionUID = 1905267726859760887L;

  @Id
  private String id;
  private String clientId;
  private String title;
  private String description;
  private String objective;
  private List<SocialPlatform> platformList;
  private String author;
  private List<Budget> budgetList;
  private Location location;
  private long startDate;
  private long endDate;
  private int status;
  private List<Brand> brands;
  private List<SocialHandler> handles;
  private List<String> keywords;
  private List<String> hashtags;
  @DBRef
  private Team team;
  private List<AudienceType> targetAudience;

  private List<PostMetrics> postMetrics;
  
  private String profileimageName;
  private String hashedProfileImageName;

  @Transient
  private int countPost;

}


@Data
@AllArgsConstructor
@NoArgsConstructor
class Location {
  private String country;
  private String city;
  private String region;
}
