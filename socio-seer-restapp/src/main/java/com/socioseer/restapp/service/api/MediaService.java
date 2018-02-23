package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.request.MediaRequest;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Media Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface MediaService extends CrudApi<Media> {

	/**
	 * <b>Get Media list by clientId</b>
	 * @param clientId
	 * @param filters
	 * @param pageRequest
	 * @return	returns Media list
	 */
  List<Media> getMediaByClientId(String clientId,List<Filter> filters, Pageable pageRequest);

  /**
	 * <b>Save Media</b>
	 * @param mediaRequest
	 * @param contentList
	 * @return	returns Media
	 */
  List<Media> save(MediaRequest mediaRequest, MultipartFile[] contentList);

  /**
	 * <b>Check Media exists or not</b>
	 * @param mediaId
	 * @return	returns boolean
	 */
  boolean exists(String mediaId);
  
  /**
	 * <b>Delete Media by id and deletedBy</b>
	 * @param id
	 * @param updatedBy
	 */
  void delete(String id, String updatedBy);
  
  /**
	 * <b>Get All Media list</b>
	 * @param pageable
	 * @param filters
	 * @return	returns Media list
	 */
  List<Media> getAll(Pageable pageable, List<Filter> filters);
  
  /**
	 * <b>Set image url</b>
	 * @param medias
	 */
  void setImageUrl(Optional<List<Media>> medias);

  
}
