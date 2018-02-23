package com.socioseer.integration.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.model.JobExecutionInfo;

public interface JobExecutionInfoDao extends MongoRepository<JobExecutionInfo, String> {
  JobExecutionInfo findByJobName(String jobName);
}
