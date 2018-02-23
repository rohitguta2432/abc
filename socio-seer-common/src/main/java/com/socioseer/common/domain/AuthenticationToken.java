package com.socioseer.common.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.Id;
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
@EqualsAndHashCode(of = { "id", "userId" })
@Document(collection = "authenticationtoken")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@JsonInclude(content = Include.NON_NULL)
public class AuthenticationToken {

	@Id
	private String id;
	private final String userId;
	private final String authToken;
	private final String client;
	private final long creatDate;
	private long lastAccessed;

}
