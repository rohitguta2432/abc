package com.socioseer.integration.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.PlatformConstant;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.MongoDao;
import com.socioseer.integration.service.api.PlatFormPostService;
import com.socioseer.integration.service.util.UUIDUtil;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("platformPostService")
public class PlatFormPostServiceImpl implements PlatFormPostService<Map<String, Object>> {

  private static final String TWITTER_COMMENT_COLLECTION = "twitter-comment";

  @Autowired
  private MongoDao<Map<String, Object>> mongoDao;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public Map<String, Object> save(@NonNull String collectionName,
      @NonNull Map<String, Object> data) {

    if (CollectionUtils.isEmpty(data)) {
      log.warn("Social post data cannot be empty.");
      return Collections.emptyMap();
    }
    try {
      data.put(ModelConstants.ID, UUIDUtil.generateId());
      data.put(ModelConstants.CAPTURED_AT, System.currentTimeMillis());
      mongoDao.save(collectionName, data);

      if (PlatformConstant.TWITTER.equalsIgnoreCase(collectionName)
          && data.containsKey("in_reply_to_status_id")
          && !ObjectUtils.isEmpty(data.get("in_reply_to_status_id"))) {
        updateCommentCount(data.get("in_reply_to_status_id").toString(),
            data.get("id_str").toString());
      }
      return data;
    } catch (Exception e) {
      log.error("Error while saving social post", e);
      throw new SocioSeerException("Error while saving social post", e);
    }
  }

  private void updateCommentCount(String inReplyToStatusId, String tweetId) {
    Query searchQuery = new Query(Criteria.where(ModelConstants.ID).is(tweetId)
        .and("in_reply_to_status_id").is(inReplyToStatusId));
    Map<String, Object> existingTweetCount = (Map<String, Object>) mongoTemplate
        .findOne(searchQuery, Map.class, TWITTER_COMMENT_COLLECTION);

    if (CollectionUtils.isEmpty(existingTweetCount)) {
      existingTweetCount = new HashMap<>();
      existingTweetCount.put(ModelConstants.ID, tweetId);
      existingTweetCount.put("in_reply_to_status_id", inReplyToStatusId);
      existingTweetCount.put(ModelConstants.COMMENT_COUNT, 1);
      existingTweetCount.put(ModelConstants.CAPTURED_AT, System.currentTimeMillis());
      mongoTemplate.save(existingTweetCount, TWITTER_COMMENT_COLLECTION);
    }
  }
}
