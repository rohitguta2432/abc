package com.socioseer.common.domain.model;


import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
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
@EqualsAndHashCode(of = {"id", "clientName"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "client")
@JsonInclude(content = Include.NON_NULL)
public class Client extends AbstractEntity {

  @Id
  private String id;
  @Indexed
  private String clientName;
  private String clientLogo;
  private int noOfBrands;
  private Industry industry;
  private Segment segment;
  private SubSegment subSegment;
  private String webUrl;
  private long subscriptionStartDate;
  private long subscriptionEndDate;
  private int noOfUsers;
  private Licence licenseType;
  private String clientSocioSeerPlatformUrl;
  private int status;
  private int countUsers;

  private List<CompetitiorsDefinition> competitiorsDefinitions;
  private ClientInformation clientInformation;

  private String profileImageName;
  private String hashedProfileImageName;
  private String woeid;
  @Transient
  private String profileImageUrl;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CompetitiorsDefinition {
    private String name;
    private Map<String,List<String>> handles;
    private List<String> keywords;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ClientInformation {
    private String name;
    private String mobileNumber;
    private String officeLandline;
    private String email;
    private String designation;
    private String city;
    private String Address;
    private String country;
    private String state;
  }

}
