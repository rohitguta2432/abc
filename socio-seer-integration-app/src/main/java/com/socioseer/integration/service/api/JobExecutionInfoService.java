package com.socioseer.integration.service.api;

import com.socioseer.common.domain.model.JobExecutionInfo;

/**
 * <h3>JobExecutionInfo Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface JobExecutionInfoService {
  
  /**
   * <b>Save JobExecutionInfo </b>
   * @param jobExecutionInfo
   */
  void save(JobExecutionInfo jobExecutionInfo);
  
  /**
   * <b>Update JobExecutionInfoService by JobName</b>
   * @param jobName
   * @param lastExecutionTime
   */
  void updateByJobName(String jobName, long lastExecutionTime);

  /**
   * <b>Get JobExecutionInfoService by JobName</b>
   * @param jobName
   * @return	returns JobExecutionInfo	
   */
  JobExecutionInfo findByJobName(String jobName);
  
  /**
   * <b>Get Last Execution Time</b>
   * @param jobName
   * @return	returns long
   */
  long findLastExecutionTime(String jobName);
}
