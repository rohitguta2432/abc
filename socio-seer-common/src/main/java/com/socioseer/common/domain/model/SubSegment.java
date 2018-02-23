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
@EqualsAndHashCode(of = { "id", "subSegmentName" }, callSuper = false)
@Document(collection = "subSegment")
@JsonInclude(content = Include.NON_NULL)
public class SubSegment extends AbstractEntity {

	@Id
	private String id;
	private String segmentId;
	private String subSegmentName;
	private int status;
}
