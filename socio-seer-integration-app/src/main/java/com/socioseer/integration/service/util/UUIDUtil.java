package com.socioseer.integration.service.util;

import java.util.UUID;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public class UUIDUtil {

  public static String generateId() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

}
