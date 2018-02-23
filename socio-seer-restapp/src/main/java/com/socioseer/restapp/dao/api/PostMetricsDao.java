package com.socioseer.restapp.dao.api;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.model.PostMetrics;

/**
 * <h3>PostMetrics Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PostMetricsDao extends MongoRepository<PostMetrics, String> {
  
}
