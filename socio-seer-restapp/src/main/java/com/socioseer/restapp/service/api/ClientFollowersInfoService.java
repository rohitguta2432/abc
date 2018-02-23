package com.socioseer.restapp.service.api;

import java.util.List;

import com.socioseer.common.domain.model.ClientFollowersInfo;

/**
 * <h3>ClientFollowersInfo Service</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface ClientFollowersInfoService  extends CrudApi<ClientFollowersInfo>{

	/**
	 * <b>Get ClientFollowersInfo by clientId and platformId and handlerId</b>
	 * @param clientId
	 * @param platformId
	 * @param handlerId
	 * @return	returns ClientFollowersInfo
	 */
  ClientFollowersInfo findByClientIdAndPlatformAndHandlerId(String clientId, String platformId, String handlerId);
  
  /**
   * <b>Get ClientFollowersInfo list by clientId</b>
   * @param clientId
   * @return	returns ClientFollowersInfo list	
   */
  List<ClientFollowersInfo> aggregateByClientId(String clientId);
}
