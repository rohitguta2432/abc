package com.socioseer.common.domain.model.request;

import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
public class MediaRequest {
	private String id;
	private List<String> keywords;
	private List<MediaContent> contentList;
	private String clientId;
	private String createdBy;
	private String url;
	private String onlineLinkUrl;
	public List<MediaContent> getContentList() {
		return (contentList == null) ? Collections.emptyList() : contentList;
	}
	
}