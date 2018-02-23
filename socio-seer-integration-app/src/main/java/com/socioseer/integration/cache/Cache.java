package com.socioseer.integration.cache;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.integration.service.util.JsonParser;

@Slf4j
@Component
public class Cache {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  public <T> Optional<T> get(String objectKey, String key, Class<T> clazz) {
    try {
      return Optional.ofNullable(JsonParser.toObject(
          (String) redisTemplate.opsForHash().get(objectKey, key), clazz));
    } catch (Exception e) {
      log.error("error while fetching object from cache", e);
      throw new SocioSeerException(e);
    }
  }

  public <T> Optional<T> get(String objectKey, String key, TypeReference<T> type) {
    try {

      Object cacheObject = redisTemplate.opsForHash().get(objectKey, key);

      if (ObjectUtils.isEmpty(cacheObject)) {
        return Optional.empty();
      }
      return Optional.ofNullable(JsonParser.getObject((String) cacheObject, type));
    } catch (Exception e) {
      log.error("error while fetching object from cache", e);
      throw new SocioSeerException(e);
    }
  }

  /**
   * Gets the objects.
   *
   * @param <T> the generic type
   * @param objectKey the object key
   * @param clazz the clazz
   * @return the objects
   */
  public <T> List<T> getObjects(String objectKey, Class<T> clazz) {
    try {
      return redisTemplate.opsForHash().values(objectKey).stream().map(value -> {
        return JsonParser.toObject((String) value, clazz);
      }).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("error while fetching object from cache", e);
      throw new SocioSeerException(e);
    }
  }

  public <T> List<T> getObjects(String objectKey, TypeReference<T> type) {
    try {
      return redisTemplate.opsForHash().values(objectKey).stream().map(value -> {
        return JsonParser.getObject((String) value, type);
      }).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("error while fetching object from cache", e);
      throw new SocioSeerException(e);
    }
  }

  /**
   * Put.
   *
   * @param <T> the generic type
   * @param objectKey the object key
   * @param key the key
   * @param value the value
   * @the cache exception
   */
  public <T> void put(String objectKey, String key, T value) {
    try {
      redisTemplate.opsForHash().put(objectKey, key, JsonParser.toJson(value));
    } catch (Exception e) {
      log.error("error while saving object in cache", e);
      throw new SocioSeerException(e);
    }
  }

}
