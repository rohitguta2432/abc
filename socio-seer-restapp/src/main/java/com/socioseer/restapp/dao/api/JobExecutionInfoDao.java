package com.socioseer.restapp.dao.api;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.model.JobExecutionInfo;

/**
 * <h3>JobExecutionInfo Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface JobExecutionInfoDao extends MongoRepository<JobExecutionInfo, String>{

	/**
	 * 
	 * @param jobName
	 * @return		returns JobExecutionInfo
	 */
	JobExecutionInfo findByJobName(String jobName);
}
