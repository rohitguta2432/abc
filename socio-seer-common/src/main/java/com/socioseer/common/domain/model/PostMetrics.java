package com.socioseer.common.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@NoArgsConstructor
@Document(collection = "post_metrics")
public class PostMetrics extends AbstractEntity{
  @Id
  private String id;
  private String clientId;
  private String name;
  private String unit;
  private List<Integer> level;
  private Boolean isNotify;
 
}
