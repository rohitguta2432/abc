package com.socioseer.restapp.service.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.socioseer.common.domain.model.campaign.enums.MediaType;

public class MediaUtil {

	private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp|jpeg))$)";

	private static final String VIDEO_PATTERN = "([^\\s]+(\\.(?i)(avi|wmv|flv|mpg|mp4))$)";

	public static MediaType getMediaType(String fileName) {
		if (isValidImage(fileName.substring(fileName.lastIndexOf(" ")+1).toLowerCase())) {
			return MediaType.IMAGE;
		} else if (isValidVideo(fileName)) {
			return MediaType.VIDEO;
		}
		return null;
	}

	public static boolean isValidImage(String fileName) {
		Pattern pattern = Pattern.compile(IMAGE_PATTERN);
		Matcher matcher = pattern.matcher(fileName);
		return matcher.matches();
	}

	public static boolean isValidVideo(String fileName) {
		Pattern pattern = Pattern.compile(VIDEO_PATTERN);
		Matcher matcher = pattern.matcher(fileName);
		return matcher.matches();
	}

}
