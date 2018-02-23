package com.socioseer.common.domain.model;

import java.util.Date;

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
@Document(collection = "job_execution_info")
public class JobExecutionInfo {
  @Id
  private String id;
  private String jobName;
  private Date lastExecutionTime;
}
