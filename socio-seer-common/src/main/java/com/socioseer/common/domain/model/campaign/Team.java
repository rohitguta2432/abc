package com.socioseer.common.domain.model.campaign;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "team")
public class Team {
    @Id
    private String id;
    private String clientId;
    private String name;
    private List<String> users;
    private String contentApprover;

}
