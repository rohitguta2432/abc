package com.socioseer.restapp.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.socioseer.common.constants.StatusConstants;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.Team;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.campaign.SocialPlatform;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.request.TeamReportRequest;
import com.socioseer.common.domain.model.response.TeamReport;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.service.api.PerformanceService;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.api.PostService;
import com.socioseer.restapp.service.api.SocialHandlerService;
import com.socioseer.restapp.service.api.SocialPlatformService;
import com.socioseer.restapp.service.api.TeamService;
import com.socioseer.restapp.service.api.UserService;
import com.socioseer.restapp.service.util.DateUtil;

/**
 * <h3>PerformanceService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Service
@Slf4j
public class PerformanceServiceImpl implements PerformanceService {

  private static final String DATE_PATTERN = "dd/MM/yyyy";
  
  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private CampaignService campaignService;

  @Autowired
  private TeamService teamService;

  @Autowired
  private PostService postService;

  @Autowired
  private SocialPlatformService socialPlatformService;

  @Autowired
  private SocialHandlerService socialHandlerService;
  
  @Autowired
  private PostScheduleService postScheduleService;
  
  @Autowired
  private UserService userService;

  /**
   * <b>Get TeamReport list</b>
   * @param teamReportRequest
   * @return	returns TeamReport list
   */
  @Override
  public List<TeamReport> teamReport(TeamReportRequest teamReportRequest) {
    validate(teamReportRequest);
    String clientId = teamReportRequest.getClientId();
    String authorId= teamReportRequest.getAuthorId();
    
    Map<String, Team> campaignIdTeamMap = getAllCampaignIds(clientId, 
        DateUtil.getDate((teamReportRequest.getStartDate()), DATE_PATTERN), 
        DateUtil.getDate((teamReportRequest.getEndDate()), DATE_PATTERN));
    
    List<SocialHandler> socialHandlers = getSocialHandlerIds(clientId, teamReportRequest.getPlatformIds());
    List<Post> posts = postService.findAllByCampaignIdAndSocialHandlers(campaignIdTeamMap.keySet() , socialHandlers);

    Map<String, Map<Integer, List<Post>>> campaignPostStatusMap =
        posts.stream().collect(
            Collectors.groupingBy(Post::getCampaignId,
                Collectors.groupingBy(Post::getStatus, Collectors.toList())));

    List<TeamReport> teamReports = new ArrayList<TeamReport>();
    campaignPostStatusMap.forEach((key, entry) -> {
      teamReports.add(buildReport(campaignIdTeamMap.get(key), entry, authorId));
    });
    return teamReports;
  }

  /**
   * <b>Validate TeamReport</b>
   * @param teamReportRequest
   */
  private void validate(TeamReportRequest teamReportRequest) {
    if(StringUtils.isEmpty(teamReportRequest.getClientId())){
      log.debug("Client id cannot be null/empty");
      throw new SocioSeerException("Client id cannot be null/empty");
    }
    
  }

  /**
   * <b>Build TeamReport</b>
   * @param team
   * @param entry
   * @param authorId
   * @return	returns TeamReport
   */
  private TeamReport buildReport(Team team, Map<Integer, List<Post>> entry, String authorId) {
    TeamReport teamReport = new TeamReport();
    teamReport.setName(team.getName());
    teamReport.setPostCount(entry.values().stream().mapToInt(List::size).sum());
    teamReport.setPendingCount(getSize(authorId, entry.get(StatusConstants.CAMPAIGN_POST_PENDING)));
    List<Post> approvedPosts = entry.get(StatusConstants.CAMPAIGN_POST_APPROVED);
    teamReport.setApprovedCount(getSize(authorId, approvedPosts));
    teamReport.setRejectedCount(getSize(authorId,entry.get(StatusConstants.CAMPAIGN_POST_REJECTED)));
    teamReport.setPubishedCount(getPublishedPostCount(approvedPosts));
    return teamReport;
  }

  /**
   * <b>Get count of published post</b>
   * @param approvedPosts
   * @return	returns integer
   */
  private int getPublishedPostCount(List<Post> approvedPosts) {
    if(CollectionUtils.isEmpty(approvedPosts)){
      return 0;
    }
    
    return (int) approvedPosts.stream().map(
        post-> postScheduleService.findPublishedPost(post.getId())).collect(Collectors.toList()).size();
  }

  /**
   * <b>Get Post Size</b>
   * @param authorId
   * @param posts
   * @return	returns integer
   */
  private int getSize(String authorId, List<Post> posts) {
    if(CollectionUtils.isEmpty(posts)){
      return 0;
    }
    
    if(StringUtils.isEmpty(authorId)){    
      return posts.size();
    }
    return (int)posts.stream().filter(post-> StringUtils.equals(post.getApprovedBy(), authorId)).count();
  }

  /**
   * <b>Get SocialHandler by clientId and platformIds list</b>
   * @param clientId
   * @param platformIds
   * @return	returns SocialHandler list
   */
  private List<SocialHandler> getSocialHandlerIds(String clientId, List<String> platformIds) {
    if (CollectionUtils.isEmpty(platformIds)) {
      return Collections.emptyList();
    }

    return platformIds
        .stream()
        .map(
            platformId -> {
              SocialPlatform socialPlatform = socialPlatformService.get(platformId);
              return socialHandlerService.getSocialHandlerByClientIdAndSocialPlatform(clientId,
                  socialPlatform);
            }).flatMap(x -> x.stream())
        .collect(Collectors.toList());
  }

  /**
   * <b>Get All CampaignIds by clientId and startDate and endDate</b>
   * @param clientId
   * @param startDate
   * @param endDate
   * @return	returns map object
   */
  private Map<String, Team> getAllCampaignIds(@NonNull String clientId, Date startDate, Date endDate) {
    List<Campaign> campaigns = campaignService.getAllCampaignsByClientId(clientId);
    if (CollectionUtils.isEmpty(campaigns)) {
      throw new SocioSeerException("No campaign found for client id " + clientId);
    }
    
    Map<String,Team>  campaignTeamMap = new HashMap<String, Team>();
    campaigns.forEach((campaign) -> campaignTeamMap.put(campaign.getId(), campaign.getTeam()));
    return campaignTeamMap;
  }

}
