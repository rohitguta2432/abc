package com.socioseer.common.domain.model;

import java.util.List;

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
@EqualsAndHashCode(of = { "id",  "name" }, callSuper = false)
@Document(collection = "country")
@JsonInclude(content = Include.NON_NULL)
public class Country extends AbstractEntity {

	@Id
	private String id;
	private String name;
	private String code;
	private int status;
	private String woeid;
	private List<State> states;
	
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonInclude(content = Include.NON_NULL)
	public static class State {
	    private String name;
	    private String abbrevation;
	}

}
