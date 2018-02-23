package com.socioseer.integration.service.api;


/**
 * <h3>SocialPost Aggregator</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface SocialPostAggregator<T> {

	/**
	 * 
	 * @param lastCapturedAt
	 * @return	returns T
	 */
  T aggregateData(long lastCapturedAt);

}
