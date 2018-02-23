package com.socioseer.common.domain.model.campaign;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.socioseer.common.domain.model.AbstractEntity;
import com.socioseer.common.domain.model.campaign.enums.MediaType;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "media")
@EqualsAndHashCode(of = {"id", "clientId"}, callSuper = false)
@JsonInclude(content = Include.NON_NULL)
public class Media extends AbstractEntity {
  @Id
  private String id;
  private List<String> keywords;
  private String videoUrl;
  private MediaType mediaType;
  private String originalFileName;
  private String hashFileName;
  private String clientId;
  private int status;
  
  private String onlineLinkUrl;
  @Transient
  private String url;
  
  @Builder
  public Media(String clientId, List<String> keywords, String videoUrl, MediaType mediaType,
      String originalFileName, String hashFileName, int status, Long createdDate, String createdBy,
      Long updatedDate, String updatedBy ,String onlineLinkUrl) {
    super(createdDate, createdBy, updatedDate, updatedBy);
    this.clientId = clientId;
    this.keywords = keywords;
    this.videoUrl = videoUrl;
    this.mediaType = mediaType;
    this.originalFileName = originalFileName;
    this.hashFileName = hashFileName;
    this.status = status;
    this.onlineLinkUrl =onlineLinkUrl;
  }

}
