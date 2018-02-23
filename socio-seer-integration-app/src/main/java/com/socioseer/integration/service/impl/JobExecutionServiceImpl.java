package com.socioseer.integration.service.impl;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.socioseer.common.domain.model.JobExecutionInfo;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.dao.JobExecutionInfoDao;
import com.socioseer.integration.service.api.JobExecutionInfoService;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Service
@Slf4j
public class JobExecutionServiceImpl implements JobExecutionInfoService {

  @Autowired
  private JobExecutionInfoDao jobExecutionInfoDao;

  @Override
  public void save(JobExecutionInfo jobExecutionInfo) {
    jobExecutionInfoDao.save(jobExecutionInfo);
  }

  @Override
  public void updateByJobName(String jobName, long lastExecutionTime) {
    try {
      JobExecutionInfo jobExecutionInfo = jobExecutionInfoDao.findByJobName(jobName);
      if (ObjectUtils.isEmpty(jobExecutionInfo)) {
        jobExecutionInfo = new JobExecutionInfo();
        jobExecutionInfo.setJobName(jobName);
      }
      jobExecutionInfo.setLastExecutionTime(new Date(lastExecutionTime));
      jobExecutionInfoDao.save(jobExecutionInfo);
    } catch (Exception e) {
      String message = "Error while getting job execeution info";
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }


  @Override
  public JobExecutionInfo findByJobName(String jobName) {
    try {
      return jobExecutionInfoDao.findByJobName(jobName);
    } catch (Exception e) {
      String message = "Error while getting job execeution info";
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

  @Override
  public long findLastExecutionTime(String jobName) {
    JobExecutionInfo jobExecutionInfo = findByJobName(jobName);
    if (jobExecutionInfo == null) {
      jobExecutionInfo = new JobExecutionInfo();
      jobExecutionInfo.setJobName(jobName);
      jobExecutionInfo.setLastExecutionTime(new Date(System.currentTimeMillis()));
      save(jobExecutionInfo);
    }
    return jobExecutionInfo.getLastExecutionTime().getTime();
  }

}
