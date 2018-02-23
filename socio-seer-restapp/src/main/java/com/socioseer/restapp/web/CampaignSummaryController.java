package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.CampaignSummaryService;

/**
 * <h3>This Controller Manage the All API of Campaign Summary.</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "campaign-summary", produces = MediaType.APPLICATION_JSON_VALUE)
public class CampaignSummaryController {

  @Autowired
  private CampaignSummaryService campaignSummaryService;

  /**
   * <b>Save CampaignSummary</b>
   * @param campaignSummary		campaignSummary json
   * @return				returns CampaignSummary
   * <b></br>URL FOR API :</b>	/api/admin/campaign-summary 
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Response<CampaignSummary>> save(@RequestBody CampaignSummary campaignSummary) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Campaign summary saved successfully",
        campaignSummaryService.save(campaignSummary)), HttpStatus.OK);
  }
  
  /**
   * <b>Get CampaignSummary by postId</b>
   * @param postId
   * @return		returns CampaignSummary
   * <b></br>URL FOR API :</b>	/api/admin/campaign-summary/{postId}
   */
  @RequestMapping(value="{postId}",method = RequestMethod.GET)
  public ResponseEntity<Response<CampaignSummary>> getCampaignSummaryByPostId(@PathVariable String postId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Campaign summary fetched successfully",
        campaignSummaryService.getCampaignSummaryByPostId(postId)), HttpStatus.OK);
  }

  /**
   * <b>Get CampaignSummary by postIds</b>
   * @param postids	list of postId
   * @return		campaignSummary list
   * <b></br>URL FOR API :</b> /api/admin/campaign-summary/get/postIds
   */
  @RequestMapping(value="get/postIds",method = RequestMethod.POST)
  public ResponseEntity<Response<List<CampaignSummary>>> getCampaignSummaryByPostIds(@RequestBody List<String>  postids) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(), "Campaign summary fetched successfully",
        campaignSummaryService.getCampaignSummaryByPostIds(postids)), HttpStatus.OK);
  }
}
