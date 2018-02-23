package com.socioseer.restapp.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.socioseer.common.domain.model.CampaignSchedulerDto;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.util.JsonParser;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Campaign.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "campaign", produces = MediaType.APPLICATION_JSON_VALUE)
public class CampaignController {

  @Autowired
  CampaignService campaignService;

  /**
   * <b>Save Campaign</b>
   * @param campaign	Campaign Details Json
   * @param logo		multipart image file of campaign logo
   * @return		returns Campaign
   * <b></br>URL FOR API :</b>	/api/admin/campaign   	
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Response<Campaign>> saveCampaign(@RequestParam(value = "campaign") String campaign ,@RequestParam(value = "logo", required = false) MultipartFile logo) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Campaign saved successfully",
        campaignService.save(JsonParser.toObject(campaign, Campaign.class) ,logo)), HttpStatus.OK);
  }
 
  /**
   * <b>Get Campaign by campaignId</b>
   * @param id	campaignId
   * @return	returns Campaign
   * <b></br>URL FOR API :</b>	/api/admin/campaign/5940d46a32753232cb200e1f  
   */
  @RequestMapping(value = "{id}", method = RequestMethod.GET)
  public ResponseEntity<Response<Campaign>> getCampaign(@PathVariable String id) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Campaign fetched successfully", campaignService.get(id)), HttpStatus.OK);
  }

  /**
   * <b>Update Campaign </b>
   * @param id			campaignId
   * @param campaign	Campaign Details Json
   * @param logo		multipart image file of campaign logo
   * @return			returns Campaign
   * <b></br>URL FOR API :</b>	/api/admin/campaign/5940d46a32753232cb200e1f  
   */
  @RequestMapping(value = "{id}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Campaign>> updateCampaign(@PathVariable String id,
		  @RequestPart(value = "campaign", required = false) String campaign, @RequestParam(value = "logo",
	      required = false) MultipartFile logo) {
	  if (StringUtils.isEmpty(campaign)) {
		  campaign = "{}";
	    }
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Campaign updated successfully", campaignService.update(id,JsonParser.toObject(campaign, Campaign.class), logo)), HttpStatus.OK);
  }
 
  /**
   * <b>Get All Campaign</b>
   * @param query
   * @param pageable
   * @return			returns campaigns list
   * <b></br>URL FOR API :</b>	/api/admin/campaign/all
   */
  @RequestMapping(value = "all", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Campaign>>> getAllCampaign(@RequestParam("q") String query,
      Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Campaigns fetched successfully",
            campaignService.getAllCampaigns(pageable, QueryParser.parse(query)),
            campaignService.getAllCampaigns(null, QueryParser.parse(query)).size()),
        HttpStatus.OK);
  }
  
  /**
   * <b>Get campaigns list of client by clientId</b>
   * @param id			clientId
   * @param query
   * @param pageable
   * @return			returns campaigns list
   * <b></br>URL FOR API :</b>	/api/admin/campaign/client/{clientId}
   */
  @RequestMapping(value = "client/{id}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Campaign>>> getAllCampaignsByClientId(@PathVariable String id,
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {

    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Campaign fetched successfully",
            campaignService.getAllCampaignsByClientId(id, QueryParser.parse(query), pageable),
            campaignService.getAllCampaignsByClientId(id, QueryParser.parse(query), null).size()),
        HttpStatus.OK);
  }

  /**
   * 
   * @param id			userId 
   * @param query
   * @param pageable	
   * @return			returns campaigns lists
   * <b></br>URL FOR API :</b>	/api/admin/campaign/user/{userId}
   */
  @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Campaign>>> getAllCampaignsByUserId(@PathVariable String id,
      @RequestParam("q") String query, Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Campaign fetched successfully",
            campaignService.getAllCampaignsByUserId(id, QueryParser.parse(query), pageable),
            campaignService.getAllCampaignsByUserId(id, QueryParser.parse(query), null).size()),
        HttpStatus.OK);
  }

  /**
   * 
   * @param id			campaignId	
   * @param status		status	
   * @param updatedBy	
   * @return			returns boolean
   * <b></br>URL FOR API :</b>	/api/admin/campaign/status/{campaignId}/{status}/{updatedBy} 		
   */
  @RequestMapping(value = "status/{id}/{status}/{updatedBy}", method = RequestMethod.PUT)
  public ResponseEntity<Response<Boolean>> changeStatus(@PathVariable("id") String id,
      @PathVariable("status") int status, @PathVariable("updatedBy") String updatedBy) {
    campaignService.changeStatus(id, status, updatedBy);
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Compaign status updated successfully.", true),
        HttpStatus.OK);
  }

  /**
   * 
   * @param clientId	
   * @param startDate	campaignStartDate
   * @param endDate		campaignEndDate	
   * @param query
   * @param pageable
   * @return			returns map object
   * <b></br>URL FOR API :</b>	/api/admin/campaign/client/{clientId}/{startDate}/{endDate}
   */
  @RequestMapping(value = "client/{clientId}/{startDate}/{endDate}", method = RequestMethod.GET)
  public ResponseEntity<Response<Map<Long, List<CampaignSchedulerDto>>>> filterByClient(
      @PathVariable(value = "clientId") String clientId,
      @PathVariable(value = "startDate") Long startDate,
      @PathVariable(value = "endDate") Long endDate,
      @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Compaign fetched successfully.", campaignService.filterCampaignScheduleByClientId(clientId,
            QueryParser.parse(query), pageable, startDate, endDate)),
        HttpStatus.OK);
  }
  
  /**
   * 
   * @param id			userId
   * @param query
   * @param pageable
   * @return
   * <b></br>URL FOR API :</b>	/api/admin/campaign/user/{userId}/team				
   */
  @RequestMapping(value = "user/{id}/team", method = RequestMethod.GET)
  public ResponseEntity<Response<List<Campaign>>> getAllCampaignsByUserIdTeam(@PathVariable String id,
		  @RequestParam(value = "q", required = false) String query, Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(), "Campaign fetched successfully",
            campaignService.getAllCampaignsByUserIdAndTeam(id, QueryParser.parse(query), pageable),
            campaignService.getAllCampaignsByUserIdAndTeam(id, QueryParser.parse(query), null).size()),
        HttpStatus.OK);
  }
}
