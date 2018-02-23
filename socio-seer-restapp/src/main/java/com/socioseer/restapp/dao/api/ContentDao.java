package com.socioseer.restapp.dao.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.campaign.Content;

/**
 * <h3>Content Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface ContentDao extends PagingAndSortingRepository<Content, String> {

	/**
	 * 
	 * @param clientId
	 * @param pageable
	 * @return		returns Contents
	 */
	Page<Content> findByClientId(String clientId, Pageable pageable);

}
