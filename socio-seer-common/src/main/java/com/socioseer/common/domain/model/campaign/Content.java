package com.socioseer.common.domain.model.campaign;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
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
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "id", "clientId" }, callSuper = false)
@Document(collection = "content")
@JsonInclude(content = Include.NON_NULL)
public class Content extends AbstractEntity {

	@Id
	private String id;

	private String brandId;

	private String clientId;

	private String campaignId;

	private int status;

	@DBRef
	private List<SocialPlatform> platforms;

	@DBRef
	private List<Media> media;

	private List<String> tags;

	private String title;

	private String author;

	private String authorName;

	private String postURL;

	private String postDescription;
	
	private String onlineLinkUrl;

}
