package com.socioseer.restapp.service.api;

import java.util.Date;

import com.socioseer.common.domain.model.JobExecutionInfo;

/**
 * <h3>JobExecution Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface JobExecutionInfoService extends CrudApi<JobExecutionInfo>{
  
	/**
	   * <b>Update JobExecutionInfo by jobName and lastExecutionTime</b>
	   * @param jobName
	   * @param lastExecutionTime
	   */
  void updateByJobName(String jobName, Date lastExecutionTime);

  /**
   * <b>Get JobExecutionInfo by JobName</b>
   * @param jobName
   * @return	returns JobExecutionInfo
   */

  JobExecutionInfo findByJobName(String jobName);
}
