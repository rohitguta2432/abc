package com.socioseer.integration.service.util;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
public class JsonParser {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * 
   * @param jsonAsString
   * @param clazz
   * @return	returns T
   */
  public static <T> T toObject(String jsonAsString, Class<T> clazz) {
    try {
      return OBJECT_MAPPER.readValue(jsonAsString, clazz);
    } catch (Exception e) {
      String message = String.format("Error while converting json to : %s", clazz.getSimpleName());
      log.info(message, e);
      throw new IllegalArgumentException(message, e);
    }
  }

  /**
   * 
   * @param value
   * @param clazz
   * @return	returns T
   * @throws JsonParseException
   * @throws JsonMappingException
   * @throws IOException
   */
  public static <T> T getObject(String value, Class<T> clazz) throws JsonParseException,
      JsonMappingException, IOException {
    return OBJECT_MAPPER.readValue(value, clazz);
  }

  /**
   * 
   * @param value
   * @param type
   * @return	returns T
   */
  public static <T> T getObject(String value, TypeReference<T> type) {
    try {
      return OBJECT_MAPPER.readValue(value, type);
    } catch (Exception e) {
      String message =
          String.format("Error while converting json to : %s", type.getClass().getSimpleName());
      log.info(message, e);
      throw new IllegalArgumentException(message, e);
    }
  }

  /**
   * 
   * @param value
   * @return	returns V
   * @throws JsonProcessingException
   */
  public static <V> String toJson(V value) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(value);
  }

}
