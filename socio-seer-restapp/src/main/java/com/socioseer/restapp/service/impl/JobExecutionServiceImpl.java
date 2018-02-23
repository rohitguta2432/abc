package com.socioseer.restapp.service.impl;

import java.util.Calendar;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.socioseer.common.domain.model.JobExecutionInfo;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.JobExecutionInfoDao;
import com.socioseer.restapp.service.api.JobExecutionInfoService;

/**
 * <h3>JobExecutionService Implementation</h3>
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

  /**
   * <b>Save JobExecutionInfo</b>
   * @param jobExecutionInfo
   * @return	returns JobExecutionInfo
   */
  @Override
  public JobExecutionInfo save(JobExecutionInfo jobExecutionInfo) {
    try {
      return jobExecutionInfoDao.save(jobExecutionInfo);
    } catch (Exception e) {
      String message = "Error while saving job execeution info";
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update JobExecutionInfo</b>
   * @param id
   * @param entity
   * @return	returns JobExecutionInfo
   */
  @Override
  public JobExecutionInfo update(String id, JobExecutionInfo entity) {
    try {
      JobExecutionInfo jobExecutionInfo = jobExecutionInfoDao.findOne(id);
      if (ObjectUtils.isEmpty(jobExecutionInfo)) {
        String message = String.format("No execution details are found to update with id : %s", id);
        log.info(message);
        throw new IllegalArgumentException(message);
      }
      entity.setId(id);
      return jobExecutionInfoDao.save(entity);
    } catch (Exception e) {
      String message = "Error while saving job execeution info";
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get JobExecutionInfo by id</b>
   * @param id
   * @return	returns JobExecutionInfo
   */
  @Override
  public JobExecutionInfo get(String id) {
    try {
      return jobExecutionInfoDao.findOne(id);
    } catch (Exception e) {
      String message = "Error while getting job execeution info";
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update JobExecutionInfo by jobName and lastExecutionTime</b>
   * @param jobName
   * @param lastExecutionTime
   */
  @Override
  public void updateByJobName(String jobName, Date lastExecutionTime) {
    try {
      JobExecutionInfo jobExecutionInfo = jobExecutionInfoDao.findByJobName(jobName);
      if (ObjectUtils.isEmpty(jobExecutionInfo)) {
        jobExecutionInfo = new JobExecutionInfo();
        jobExecutionInfo.setJobName(jobName);
      }
      jobExecutionInfo.setLastExecutionTime(lastExecutionTime);
      jobExecutionInfoDao.save(jobExecutionInfo);
    } catch (Exception e) {
      String message = "Error while getting job execeution info";
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }


  /**
   * <b>Get JobExecutionInfo by JobName</b>
   * @param jobName
   * @return	returns JobExecutionInfo
   */
  @Override
  public JobExecutionInfo findByJobName(String jobName) {
    try {
      return jobExecutionInfoDao.findByJobName(jobName);
    } catch (Exception e) {
      String message = "Error while getting job execeution info";
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }
}
