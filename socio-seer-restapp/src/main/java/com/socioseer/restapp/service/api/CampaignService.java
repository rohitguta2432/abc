package com.socioseer.restapp.service.api;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.domain.model.CampaignSchedulerDto;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.dto.Filter;

/**
 * <h3>Campaign Services</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface CampaignService extends CrudApi<Campaign> {

/**
   * <b>Delete Campaign by id</b>
   * @param		id
   */
  void delete(String id);
  
  /**
   * <b>Get Campaign list by clientId</b>
   * @param		clientId
   * @param		filters
   * @param		pageable
   * @return	returns Campaign list
   */
  List<Campaign> getAllCampaignsByClientId(String clientId, List<Filter> filters, Pageable pageable);

  /**
   * <b>Get Campaign by userId</b>
   * @param		userId
   * @param		filters
   * @param		pageable
   * @return	returns Campaign list
   */
  List<Campaign> getAllCampaignsByUserId(String userId, List<Filter> filters, Pageable pageable);

  /**
   * <b>Change Campaign Status by id</b>
   * @param		id
   * @param		status
   * @param		updatedBy
   */
  void changeStatus(String id, int status, String updatedBy);

  /**
   * <b>Get Count of Campaign</b>
   * @return	returns integer
   */
  int countAll();

  /**
   * <b>Get All Campaign list</b>
   * @param		pageable
   * @param		filters
   * @return	returns Campaign list
   */
  List<Campaign> getAllCampaigns(Pageable pageable, List<Filter> filters);

  /**
   * <b>Get CampaignSchedulerDto list by clientId</b>
   * @param		clientId
   * @param		filters
   * @param		pageable
   * @param		startDate	long data
   * @param		endDate		long data
   * @return	returns map object
   */
  Map<Long, List<CampaignSchedulerDto>> filterCampaignScheduleByClientId(String clientId,
      List<Filter> filters, Pageable pageable, Long startDate, Long endDate);

  /**
   * <b>Get Campaign list by clientId</b>
   * @param		clientId
   * @param		filters
   * @param		pageable
   * @return	returns Campaign list
   */
  List<Campaign> getAllCampaignsByClientId(String clientId);

  /**
   * <b>Get Active Campaign HashTag</b>
   * @param		currentTime
   * @return	returns map object 
   */
  Map<String, List<String>> getActiveCampaignHashTags(long currentTime);

  /**
   * <b>Save Campaign</b>
   * @param		campaign
   * @param		profilePicture
   * @return	returns Campaign
   */
  Campaign save(Campaign campaign, MultipartFile profilePicture);

  /**
   * <b>Update Campaign</b>
   * @param		id
   * @param		campaign
   * @param		profilePicture
   * @return	returns Campaign
   */
  Campaign update(String id, Campaign campaign, MultipartFile profilePicture);

  /**
   * <b>Put Active Campaign Facebook Post to Cache </b>
   * @param currentTime
   */
  void putActiveCampaignFBPostToCache(long currentTime);

  /**
   * <b>Get Campaign list by userId and team</b>
   * @param		userId
   * @param		filters
   * @param		pageable
   * @return	returns Campaign list
   */
  List<Campaign> getAllCampaignsByUserIdAndTeam(String userId, List<Filter> filters,
      Pageable pageable);

  /**
   * <b>Put Campaign Detail to Cache</b>
   * @param campaignList
   */
  void putCampaignDataIntoCache(List<Campaign> campaignList);
  
  
  void putCampaignIntoCacheOnApplicationStartup();

}
