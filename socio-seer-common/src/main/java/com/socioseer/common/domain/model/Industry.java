package com.socioseer.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


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
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "industry")
@EqualsAndHashCode(of = { "id" ,"industryName" }, callSuper = false)
@JsonInclude(content = Include.NON_NULL)
public class Industry extends AbstractEntity {

	@Id
	private String id;
	private String industryName;
	private int status;
}
