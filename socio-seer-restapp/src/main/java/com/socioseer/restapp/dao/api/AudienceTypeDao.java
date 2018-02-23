package com.socioseer.restapp.dao.api;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.model.AudienceType;

/**
 * <h3>AudienceType Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface AudienceTypeDao extends MongoRepository<AudienceType, String> {
  
}
