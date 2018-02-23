package com.socioseer.restapp.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;
import com.socioseer.common.domain.model.request.TeamReportRequest;
import com.socioseer.common.domain.model.response.TeamReport;
import com.socioseer.common.dto.CampaignSummaryDetailDto;
import com.socioseer.common.dto.CampaignSummaryDto;
import com.socioseer.common.dto.Response;
import com.socioseer.restapp.service.api.CampaignHashTagSummaryService;
import com.socioseer.restapp.service.api.CampaignSummaryService;
import com.socioseer.restapp.service.api.PerformanceService;
import com.socioseer.restapp.util.QueryParser;

/**
 * <h3>This Controller Manage the All API of Performance .</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@RestController
@RequestMapping(value = "performance", produces = MediaType.APPLICATION_JSON_VALUE)
public class PerformanceController {

  @Autowired
  private PerformanceService performanceService;

  @Autowired
  private CampaignSummaryService campaignSummaryService;

  @Autowired
  private CampaignHashTagSummaryService campaignHashTagSummaryService;

  /**
   * 
   * @param teamReportRequest
   * @return					returns TeamReport list	
   * <b></br>URL FOR API :</b>	/api/admin/performance
   */
  @RequestMapping(value = "teamReport", method = RequestMethod.POST)
  public ResponseEntity<Response<List<TeamReport>>> getTeamReport(
      @RequestBody TeamReportRequest teamReportRequest) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Team report fetched successfully", performanceService.teamReport(teamReportRequest)),
        HttpStatus.OK);
  }
  
  /**
   * 
   * @param clientId
   * @param pageable
   * @return			returns CampaignSummaryDto
   * <b></br>URL FOR API :</b>	/api/admin/performance/summary/{clientId}
   */
  @RequestMapping(value = "summary/{clientId}", method = RequestMethod.GET)
  public ResponseEntity<Response<CampaignSummaryDto>> getPerformanceSummary(
      @PathVariable String clientId, @RequestParam(value = "q", required = false) String query,
      Pageable pageable) {
    return new ResponseEntity<>(
        new Response<>(HttpStatus.OK.value(),
            "Campaign summary fetched successfully", campaignSummaryService
                .getCampaignSummaryByClientId(clientId, QueryParser.parse(query), pageable)),
        HttpStatus.OK);
  }

  /**
   * 
   * @param campaignId
   * @return			returns CampaignSummaryDetailDto
   * <b></br>URL FOR API :</b>	/api/admin/performance/campaign/{campaignId}
   */
  @RequestMapping(value = "campaign/{campaignId}", method = RequestMethod.GET)
  public ResponseEntity<Response<CampaignSummaryDetailDto>> getCampaignPerformance(
      @PathVariable String campaignId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Campaign report fetched successfully",
        campaignSummaryService.getCampaignPerformance(campaignId)), HttpStatus.OK);
  }

  /**
   * 
   * @param clientId
   * @param campaignId
   * @return			returns CampaignHashTagSummary list
   * <b></br>URL FOR API :</b>	/api/admin/performance/hashtag/client/{clientId}
   */
  @RequestMapping(value = "hashtag/client/{clientId}", method = RequestMethod.GET)
  public ResponseEntity<Response<List<CampaignHashTagSummary>>> getCampaignHashTagPerformance(
      @PathVariable String clientId,
      @RequestParam(value = "campaignId", required = false) String campaignId) {
    return new ResponseEntity<>(new Response<>(HttpStatus.OK.value(),
        "Hashtags fetched successfully",
        campaignHashTagSummaryService.findByClientId(clientId)), HttpStatus.OK);
  }
}