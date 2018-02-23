package com.socioseer.common.domain;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.socioseer.common.domain.model.AbstractEntity;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@Document(collection = "roles")
@EqualsAndHashCode(of = { "id", "name" },callSuper=false)
@JsonInclude(content = Include.NON_NULL)
public class Role extends AbstractEntity{

	@Id
	private String id;
	private String name;
	private int status ;
	private List<String> permissions;
	private String displayName;
	private boolean isDefault;
	private boolean isAdmin;	
}
