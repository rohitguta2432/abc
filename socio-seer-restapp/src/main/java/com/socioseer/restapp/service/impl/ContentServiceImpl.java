package com.socioseer.restapp.service.impl;

import java.util.List;
import java.util.Optional;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableMap;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.model.Brand;
import com.socioseer.common.domain.model.Client;
import com.socioseer.common.domain.model.campaign.Content;
import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.ContentDao;
import com.socioseer.restapp.service.api.BrandService;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.api.ContentService;
import com.socioseer.restapp.service.api.MediaService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.QueryBuilder;

/**
 * <h3>ContentService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class ContentServiceImpl implements ContentService {

  @Autowired
  ContentDao contentDao;

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  private ClientService clientService;

  @Autowired
  private BrandService brandService;

  @Autowired
  private MediaService mediaService;


  /**
   * <b>Save Content</b>
   * @param		content
   * @return	returns Content
   */
  @Override
  public Content save(@NonNull Content content) {

    validateContent(content);

    try {
      content.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      content.setUpdatedDate(content.getCreatedDate());
      return contentDao.save(content);
    } catch (Exception e) {
      String message =
          String.format("Error while saving content for client : %s", content.getClientId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Update Content</b>
   * @param		id
   * @param		content
   * @return	returns Content
   */
  @Override
  public Content update(@NonNull String id, @NonNull Content content) {
    try {
      Content existingContent = contentDao.findOne(id);
      if (ObjectUtils.isEmpty(existingContent)) {
        String message = String.format("No content found with id : %s", id);
        log.info(message);
        throw new IllegalArgumentException(message);
      }
      content.setId(id);
      content.setCreatedDate(existingContent.getCreatedDate());
      content.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      contentDao.save(content);
      return content;
    } catch (Exception e) {
      String message = String.format("Error while updating content with id : %s", content.getId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get Content by id</b>
   * @param		id
   * @return	returns Content
   */
  @Override
  public Content get(@NonNull String id) {
    try {
      Content content = contentDao.findOne(id);
      setMediaUrl(content);
      return content;
    } catch (Exception e) {
      String message = String.format("Error while fetching content by id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get All Contents</b>
   * @param pageable
   * @param filters
   * @return			returns Content list
   */
  @Override
  public List<Content> getAllContent(Pageable pageable, List<Filter> filters) {

    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      List<Content> contents = mongoTemplate.find(query, Content.class);
      contents.forEach(content -> {
        setMediaUrl(content);
      });
      return contents;
    } catch (Exception e) {
      log.error("Error while fetching contents.");
      throw new SocioSeerException("Error while fetching contents.");
    }

  }

  /**
   * <b>Get All Content By clientId</b>	
   * @param clientId
   * @param filters
   * @param pageable
   * @return		  returns	Content list
   */
  @Override
  public List<Content> getAllContentsByClientId(@NonNull String clientId, List<Filter> filters,
      Pageable pageable) {
    try {
      Query query =
          QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.CLIENT_ID, clientId),
              pageable);
      return mongoTemplate.find(query, Content.class);
    } catch (Exception e) {
      String message = String.format("Error while fetching content by client id : %s", clientId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get All Content By userId</b>
   * @param userId
   * @param filters
   * @param pageable
   * @return		 returns	Content list
   */
  @Override
  public List<Content> getAllContentsByUserId(@NonNull String userId, List<Filter> filters,
      Pageable pageable) {
    try {
      Query query =
          QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.USER_ID, userId),
              pageable);
      return mongoTemplate.find(query, Content.class);
    } catch (Exception e) {
      String message = String.format("Error while fetching content by user id : %s", userId);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate Content</b>
   * @param content
   */
  private void validateContent(Content content) {

    if (StringUtils.isEmpty(content.getClientId())) {
      log.info("Client id cannot be null/empty.");
      throw new IllegalArgumentException("Client id cannot be null/empty.");
    }

    if (StringUtils.isEmpty(content.getPostDescription())) {
      log.info("Post description cannot be null/empty.");
      throw new IllegalArgumentException("Post description cannot be null/empty.");
    }

    if (StringUtils.isEmpty(content.getCreatedBy())) {
      log.info("Created by cannot be null/empty.");
      throw new IllegalArgumentException("Created by cannot be null/empty.");
    }

    Client client = clientService.get(content.getClientId());
    if (ObjectUtils.isEmpty(client)) {
      String message = String.format("Client not found by client id", content.getClientId());
      log.error(message);
      throw new IllegalArgumentException(message);
    }

    if(!StringUtils.isEmpty(content.getBrandId())){
      Brand brand = brandService.get(content.getBrandId());
      if (brand == null) {
        String message = String.format("Brand not found by brand id", content.getBrandId());
        log.error(message);
        throw new IllegalArgumentException(message);
      }
      }
  }

  /**
   * <b>Delete Content by id</b>
   * @param		id
   * @param		updatedBy
   */
  @Override
  public void delete(String id, String updatedBy) {

    try {
      Content content = contentDao.findOne(id);
      validateContentDelete(content, id);
      content.setStatus(StatusConstants.DELETED);
      content.setUpdatedBy(updatedBy);
      content.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      content = contentDao.save(content);
      String message = String.format("Content deleted by content id %s", updatedBy);
      log.info(message);
    } catch (Exception e) {
      String message = String.format("Error while fetching content by content id : %s", updatedBy);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Validate Content for Delete</b>
   * @param content
   * @param id
   */
  private void validateContentDelete(Content content, String id) {
    if (ObjectUtils.isEmpty(content)) {
      String message = String.format("Error content not find by content id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    if (content.getStatus() == StatusConstants.DELETED) {
      String message = String.format("Error role already deleted");
      log.info(message);
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * 
   * @param content
   */
  private void setMediaUrl(Content content) {
    Optional<List<Media>> medias = Optional.of(content.getMedia());
    if(!CollectionUtils.isEmpty(medias.get())){
    mediaService.setImageUrl(medias);
    content.setMedia(medias.get());
    }
    
  }

  
}
