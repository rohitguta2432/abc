package com.socioseer.common.domain.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.socioseer.common.domain.model.campaign.enums.MediaType;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@AllArgsConstructor
public class MediaContent {
	private String videoUrl;
	private MediaType mediaType;
	private String originalFileName;
	private String hashFileName;
}