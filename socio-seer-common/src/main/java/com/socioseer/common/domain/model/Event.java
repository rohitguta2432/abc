package com.socioseer.common.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Document(collection = "event")
@JsonInclude(content = Include.NON_NULL)
public class Event extends AbstractEntity {

	@Id
	private String id;
	private String clientId;
	private String eventName;
	private String description;
	private int status;
	private Long startDate;
	private Long endDate;

}
