package com.socioseer.common.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
@Document(collection = "audience-type")
@EqualsAndHashCode(of = { "id", "type" }, callSuper = false)
@JsonInclude(content = Include.NON_NULL)
public class AudienceType extends AbstractEntity {

	@Id
	private String id;
	private String name;
	private String platform;
	private String type;
	private int status; 
	private String clientId;
}
