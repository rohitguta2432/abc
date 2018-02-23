package com.socioseer.common.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = { "id",  "licenceType" }, callSuper = false)
@Document(collection = "licence")
@JsonInclude(content = Include.NON_NULL)
public class Licence extends AbstractEntity {

	@Id
	private String id;
	private String licenceType;
	private int status;
}
