package com.socioseer.restapp.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.util.JsonParser;

/**
 * Cache Operation class defines operations on cache.
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@SuppressWarnings("unchecked")
@Component
public class Cache {

  /** The redis template. */
  @Autowired
  private RedisTemplate<String, String> redisTemplate;

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

  /**
   * Multi put.
   *
   * @param <T> the generic type
   * @param objectKey the object key
   * @param keyValues the key values
   * @the cache exception
   */
  public <T> void multiPut(String objectKey, Map<String, T> keyValues) {
    try {
      Map<String, String> values = new HashMap<String, String>();
      for (Entry<String, T> entry : keyValues.entrySet()) {
        values.put(entry.getKey(), JsonParser.toJson(entry.getValue()));
      }
      redisTemplate.opsForHash().putAll(objectKey, values);
    } catch (Exception e) {
      log.error("error while saving object in cache", e);
      throw new SocioSeerException(e);
    }
  }

  /**
   * Gets the.
   *
   * @param <T> the generic type
   * @param objectKey the object key
   * @param key the key
   * @param clazz the clazz
   * @return the optional
   * @the cache exception
   */
  public <T> Optional<T> get(String objectKey, String key, Class<T> clazz) {
    try {
      return Optional.ofNullable(JsonParser.getObject(
          (String) redisTemplate.opsForHash().get(objectKey, key), clazz));
    } catch (Exception e) {
      log.error("error while fetching object from cache", e);
      throw new SocioSeerException(e);
    }
  }

  /**
   * Gets the.
   *
   * @param <T> the generic type
   * @param objectKey the object key
   * @param key the key
   * @param clazz the clazz
   * @return the optional
   * @the cache exception
   */
  public <T> Optional<T> get(String objectKey, String key, TypeReference<T> type) {
    try {

      String valueFromCache = (String) redisTemplate.opsForHash().get(objectKey, key);
      if (StringUtils.isEmpty(valueFromCache)) {
        return Optional.empty();
      }
      return Optional.ofNullable(JsonParser.getObject((String) valueFromCache, type));
    } catch (Exception e) {
      log.error("error while fetching object from cache", e);
      throw new SocioSeerException(e);
    }
  }

  /**
   * Multi get.
   *
   * @param <T> the generic type
   * @param objectKey the object key
   * @param keys the keys
   * @param clazz the clazz
   * @return the list
   * @the cache exception
   */
  @SuppressWarnings("rawtypes")
  public <T> List<T> multiGet(String objectKey, Collection<Object> keys, Class clazz) {
    try {
      return (List<T>) redisTemplate.opsForHash().multiGet(objectKey, keys).stream().map(value -> {
        return (T) JsonParser.toObject((String) value, clazz);
      }).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("error while fetching object from cache", e);
      throw new SocioSeerException(e);
    }
  }

  /**
   * Delete.
   *
   * @param key the key
   * @the cache exception
   */
  public void delete(String objectKey, String key) {
    try {
      redisTemplate.opsForHash().delete(objectKey, key);
    } catch (Exception ex) {
      throw new SocioSeerException(ex);
    }
  }

  /**
   * Delete.
   *
   * @param key the key
   * @the cache exception
   */
  public void delete(String objectKey, List<String> keys) {
    try {
      keys.stream().forEach((key -> delete(objectKey, key)));
    } catch (Exception ex) {
      throw new SocioSeerException(ex);
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


  public void delete(String objectKey) {
    try {
      redisTemplate.delete(objectKey);
    } catch (Exception e) {
      String message =
          String.format("Error while deleting object with key : %s from cache", objectKey);
      log.error(message, e);
      throw new SocioSeerException(message, e);
    }
  }

}
