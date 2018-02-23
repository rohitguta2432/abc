package com.socioseer.common.domain;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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
@Document(collection = "teams")
@JsonInclude(content = Include.NON_NULL)
public class Team extends AbstractEntity {

	@Id
	private String id;
	private String name;
	private String clientId;
	private int status;
	private List<String> usersList;
	private List<String> contentApproversList;
    
    @Transient
    private int noOfUsers;
    @Transient
    private List<User> users;
    
    @Transient
    private List<User> contentApprovers;
    
    @Transient
    private String clientName;
   
}
