package com.socioseer.restapp.dao.api;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostSchedule;

public interface PostScheduleDao extends MongoRepository<PostSchedule, String>{

  Optional<List<PostSchedule>> findByPost(Post post);
  
  Optional<List<PostSchedule>> findByRunAtBetweenAndIsActive(Date startDate, Date endDate, boolean isActive);
  
  Optional<List<PostSchedule>> findByRunAtBetweenAndIsExecutedAndIsActive(Date startDate, Date endDate, boolean isExecuted, boolean isActive);

  Optional<List<PostSchedule>> findByPostAndIsExecuted(Post post, boolean isExecuted);
  
  void deleteByPost(Post post);

}
