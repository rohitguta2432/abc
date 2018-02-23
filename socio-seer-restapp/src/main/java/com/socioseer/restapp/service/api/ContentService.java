package com.socioseer.restapp.service.api;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.socioseer.common.domain.model.campaign.Content;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Content Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface ContentService extends CrudApi<Content> {

  /**
   * <b>Get All Content By clientId</b>	
   * @param clientId
   * @param filters
   * @param pageable
   * @return		  returns	Content list
   */
  List<Content> getAllContentsByClientId(String clientId, List<Filter> filters, Pageable pageable);

  /**
   * <b>Get All Content By userId</b>
   * @param userId
   * @param filters
   * @param pageable
   * @return		 returns	Content list
   */
  List<Content> getAllContentsByUserId(String userId, List<Filter> filters, Pageable pageable);

  /**
   * <b>Delete Content by id</b>
   * @param id			contentId
   * @param updatedBy
   */
  void delete(String id, String updatedBy);

  /**
   * <b>Get All Contents</b>
   * @param pageable
   * @param filters
   * @return			returns Content list
   */
  List<Content> getAllContent(Pageable pageable, List<Filter> filters);

}
