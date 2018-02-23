package com.socioseer.common.domain.model;

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
@Document(collection = "segment")
@EqualsAndHashCode(of = { "id", "segmentName" }, callSuper = false)
@JsonInclude(content = Include.NON_NULL)
public class Segment extends AbstractEntity {

	@Id
	private String id;
	private String segmentName;
	private String industryId;
	private String industryName;
	private int status;

}
