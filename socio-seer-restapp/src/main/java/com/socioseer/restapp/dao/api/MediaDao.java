package com.socioseer.restapp.dao.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.campaign.Media;

/**
 * <h3>Licence Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface MediaDao extends PagingAndSortingRepository<Media, String> {
	/**
	 * 	
	 * @param clientId
	 * @param pageable
	 * @return	returns Media list
	 */
	Optional<List<Media>> findAllByClientId(String clientId, Pageable pageable);
}
