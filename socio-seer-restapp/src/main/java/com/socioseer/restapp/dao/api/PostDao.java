package com.socioseer.restapp.dao.api;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.model.post.Post;

/**
 * <h3>Post Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PostDao extends MongoRepository<Post, String>{
 
  /**
   * 	
   * @param campaignId
   * @return	returns Post list
   */
  List<Post> findAllByCampaignId(String campaignId);
  
  /**
   * 
   * @param campaignIds
   * @param socialHandlers
   * @return	returns Post list
   */
  List<Post> findAllByCampaignIdInAndSocialHandlersIn(Set<String> campaignIds, List<SocialHandler> socialHandlers);

}
