package com.socioseer.common.domain.model.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.socioseer.common.domain.model.AbstractEntity;
import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.request.ScheduleTime;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
@EqualsAndHashCode(callSuper = true)
public class Task extends AbstractEntity{
  @Id
  private String id;
  private String postId;
  private String campaignId;
  private String campaignTitle;
  private String approverId;
  private String approverName;
  private String clientId;
  private int status;
  
  @Transient
  private List<PostSchedule> postSchedules;
  
  @Transient
  private Post post;
  
  @Transient
  private PostSchedule postSchedule;
  
  @Transient
  private List<Media> medias;
  
  @Transient
  private String createdByName;
  
  
}
