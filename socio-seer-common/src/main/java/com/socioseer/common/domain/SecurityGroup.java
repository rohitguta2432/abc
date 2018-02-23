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
@EqualsAndHashCode(of = { "id" }, callSuper = false)
@Document(collection = "security-groups")
@JsonInclude(content = Include.NON_NULL)
public class SecurityGroup extends AbstractEntity {

	@Id
	private String id;
	private String name;
	private int status;
	private String clientId;
	private List<Role> roles;
}
