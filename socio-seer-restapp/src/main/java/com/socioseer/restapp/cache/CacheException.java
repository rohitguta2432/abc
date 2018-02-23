package com.socioseer.restapp.cache;

/**
 * The Class CacheException.
 * 
* @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
public class CacheException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1684125589861712949L;

  /**
   * Instantiates a new cache exception.
   */
  public CacheException() {}

  /**
   * Instantiates a new cache exception.
   *
   * @param message the message
   */
  public CacheException(String message) {
    super(message);
  }

  /**
   * Instantiates a new cache exception.
   *
   * @param cause the cause
   */
  public CacheException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new cache exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public CacheException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new cache exception.
   *
   * @param message the message
   * @param cause the cause
   * @param enableSuppression the enable suppression
   * @param writableStackTrace the writable stack trace
   */
  public CacheException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }



}
