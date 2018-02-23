package com.socioseer.common.domain;

import java.util.List;

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
@EqualsAndHashCode(of = {"id", "email"}, callSuper = false)
@Document(collection = "users")
@JsonInclude(content = Include.NON_NULL)
public class User extends AbstractEntity {


  @Id
  private String id;
  private String clientId;
  private String clientName;
  private String brandId;
  private String parentId;
  private String firstName;
  private String lastName;
  private String fullName;
  private String email;
  private int status;
  private String password;
  private String phone;
  @Transient
  private String rePassword;
  private List<SecurityGroup> securityGroups;
  private String profileImageName;
  private String hashedProfileImageName;
 
  @Transient
  private List<Team> teams;
  
 

}
