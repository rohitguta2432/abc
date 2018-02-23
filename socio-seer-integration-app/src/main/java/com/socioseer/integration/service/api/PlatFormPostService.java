package com.socioseer.integration.service.api;

/**
 * <h3>PlatformPost Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface PlatFormPostService<T> {

  /**
   * <b>Save PlatformPost</b>	
   * @param collectionName
   * @param data
   * @return	returns T
   */
  public T save(String collectionName, T data);

}
