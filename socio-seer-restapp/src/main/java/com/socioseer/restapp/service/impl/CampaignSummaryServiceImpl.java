package com.socioseer.restapp.service.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.campaign.Media;
import com.socioseer.common.domain.model.campaign.summary.CampaignHashTagSummary;
import com.socioseer.common.domain.model.campaign.summary.CampaignSummary;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostHandler;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.dto.CampaignReportDto;
import com.socioseer.common.dto.CampaignSummaryDetailDto;
import com.socioseer.common.dto.CampaignSummaryDto;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.dto.PlatformReportDto;
import com.socioseer.common.dto.PostReportDto;
import com.socioseer.common.dto.TopPostDto;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.CampaignSummaryDao;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.service.api.CampaignSummaryService;
import com.socioseer.restapp.service.api.ClientFollowersInfoService;
import com.socioseer.restapp.service.api.MediaService;
import com.socioseer.restapp.service.api.PostScheduleService;
import com.socioseer.restapp.service.api.PostService;
import com.socioseer.restapp.service.api.UserMentionSummaryService;
import com.socioseer.restapp.util.UrlUtil;

/**
 * <h3>CampaignSummaryService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class CampaignSummaryServiceImpl implements CampaignSummaryService {

  private static final String POST_ID = "postId";

  private static final String POST_COUNT = "postCount";

  private static final String PLATFORM = "platform";

  private static final String COMMENT_COUNT = "commentCount";

  private static final String SHARE_COUNT = "shareCount";

  private static final String LIKE_COUNT = "likeCount";

  @Autowired
  private CampaignSummaryDao campaignSummaryDao;

  @Autowired
  private PostScheduleService postScheduleService;

  @Autowired
  private CampaignService campaignService;

  @Autowired
  private PostService postService;

  @Autowired
  private MongoTemplate mongoTemplate;
  
  @Autowired
  private UrlUtil urlUtil;
  
  @Autowired
  private MediaService mediaService;

  @Autowired
  private ClientFollowersInfoService clientFollowersInfoService;

  @Autowired
  private UserMentionSummaryService userMentionSummaryService;

  /**
   * <b>Save CampaignSummary</b>
   * @param		campaignSummary
   * @return	returns CampaignSummary
   */
  @Override
  public CampaignSummary save(@NonNull CampaignSummary campaignSummary) {
    validateCampaignSummary(campaignSummary);
    PostSchedule postSchedule =
        postScheduleService.getPostScheduleBySocialPostId(campaignSummary.getSocialPostId());

    if (postSchedule == null) {
      log.info(String.format("No schedule post found for social post id : %s",
          campaignSummary.getSocialPostId()));
      return null;
    }
    
    CampaignSummary existingCampaignSummary = campaignSummaryDao.getByPostId(postSchedule.getPost().getId());
    if(existingCampaignSummary!=null){
    	
      Update updateCount = new Update();
      updateCount.set("likeCount", campaignSummary.getLikeCount());
      updateCount.inc("commentCount", campaignSummary.getCommentCount());
      updateCount.set("followersCount", campaignSummary.getFollowersCount());
      updateCount.set("retweetCount", campaignSummary.getRetweetCount());
      updateCount.set("createdTime", campaignSummary.getCreatedTime());
      mongoTemplate.updateFirst(
          new Query(Criteria.where("postId").is(existingCampaignSummary.getPostId())), updateCount,
          CampaignSummary.class);
      return existingCampaignSummary;
    }else{
      campaignSummary.setCampaignId(postSchedule.getPost().getCampaignId());
      campaignSummary.setPostId(postSchedule.getPost().getId());
      campaignSummary.setBrandId(postSchedule.getPost().getBrandId());
      campaignSummary.setClientId(postSchedule.getPost().getClientId());
      campaignSummary.setPlatform(postSchedule.getPlatform().getName());
      campaignSummary.setCreatedTime(System.currentTimeMillis());
      PostHandler postHandler =
          CollectionUtils.isEmpty(postSchedule.getPostHandlers()) ? null : postSchedule
              .getPostHandlers().get(0);
      
      if (!ObjectUtils.isEmpty(postHandler)) {
        campaignSummary.setHandlerId(postHandler.getHandlerId());
      }
      
      try {
        return campaignSummaryDao.save(campaignSummary);
      } catch (Exception e) {
        String message =
            String.format("Error while saving campaign summary for campaign id : %s",
                campaignSummary.getCampaignId());
        log.error(message, e);
        throw new SocioSeerException(message);
      }
    }
  }

  /**
   * <b>Get CampaignSummary by clientId</b>	
   * @param clientId
   * @param pageable
   * @return			returns CampaignSummaryDto
   */
  @Override
  public CampaignSummaryDto getCampaignSummaryByClientId(@NonNull String clientId,
      List<Filter> filters, Pageable pageable) {
    List<Campaign> campaigns = getCampaignByClient(clientId, filters, pageable);
    if (CollectionUtils.isEmpty(campaigns)) {
      log.info(String.format("No campaign found for client : %s", clientId));
      return null;
    }
    List<CampaignReportDto> campaignReportList = new ArrayList<CampaignReportDto>();
    CampaignReportDto campaignReportDto = null;
    List<PlatformReportDto> platformReportDtos = null;

    for (Campaign campaign : campaigns) {
      List<String> plateFormNames = new ArrayList<String>();
      campaignReportDto = new CampaignReportDto();
      platformReportDtos = getPlatformSummary(campaign.getId(), clientId);
      campaignReportDto.setCampaignId(campaign.getId());
      if (!StringUtils.isEmpty(campaign.getHashedProfileImageName())) {
        campaignReportDto.setProfileImageName(campaign.getHashedProfileImageName());
      } else {
        campaignReportDto.setProfileImageName(SocioSeerConstant.DEFAULT);
      }
      campaignReportDto.setCampaignName(campaign.getTitle());
      campaignReportDto.setStartDate(campaign.getStartDate());
      campaignReportDto.setEndDate(campaign.getEndDate());
      campaignReportDto.setTopPost(getTopPost(platformReportDtos, campaign.getId()));
      campaignReportDto.setPlatformReports(platformReportDtos);
      campaign.getPlatformList().forEach(plateForm -> {
        plateFormNames.add(plateForm.getName());
      });
      campaignReportDto.setPlatformNames(plateFormNames);
      campaignReportDto.setUserMentionSummarieList(userMentionSummaryService.aggregateByClientIdCampaign(clientId, campaign));
      campaignReportList.add(campaignReportDto);
    }
    CampaignSummaryDto campaignSummaryDto = new CampaignSummaryDto();
    campaignSummaryDto
        .setClientFollowersInfoList(clientFollowersInfoService.aggregateByClientId(clientId));
    /*campaignSummaryDto
        .setUserMentionSummarieList(userMentionSummaryService.aggregateByClientIdCampaign(clientId, campaign));*/
    campaignSummaryDto.setClientId(clientId);
    campaignSummaryDto.setCampaignReports(campaignReportList);
    
    
    Gson g = new Gson();
    
    System.out.println(g.toJson(campaignSummaryDto));
    
    return campaignSummaryDto;
  }
  
  private List<Campaign> getCampaignByClient(String clientId, List<Filter> filters,
	      Pageable pageable) {
	    if (CollectionUtils.isEmpty(filters)) {
	      filters = new ArrayList<>();
	    }
	    Filter filter = new Filter();
	    filter.setName(ModelConstants.CLIENT_ID);
	    filter.setValues(Arrays.asList(clientId));
	    filters.add(filter);
	    return campaignService.getAllCampaigns(pageable, filters);
	  }

  /**
   * <b>Get Campaign list by clientId</b>
   * @param clientId
   * @param pageable
   * @return	returns Campaign list
   */
  private List<Campaign> getCampaignByClient(String clientId, Pageable pageable) {
    Filter filter = new Filter();
    filter.setName(ModelConstants.CLIENT_ID);
    filter.setValues(Arrays.asList(clientId));
    return campaignService.getAllCampaigns(pageable, Arrays.asList(filter));
  }

  /**
   * <b>Get Campaign Performance by campaignId</b>
   * @param campaignId
   * @return			returns CampaignSummaryDetailDto
   */
  @Override
  public CampaignSummaryDetailDto getCampaignPerformance(@NonNull String campaignId) {
    Campaign campaign = campaignService.get(campaignId);
    if (ObjectUtils.isEmpty(campaign)) {
      log.info(String.format("No campaign found with id : %s", campaignId));
      throw new SocioSeerException(String.format("No campaign found with id : %s", campaignId));
    }
    List<PlatformReportDto> platformSummaryList =
        getPlatformSummary(campaignId, campaign.getClientId());
    CampaignSummaryDetailDto campaignSummaryDetailDto =
        new CampaignSummaryDetailDto(campaignId, campaign.getClientId());
    campaignSummaryDetailDto.setPlatformReports(platformSummaryList);
    campaignSummaryDetailDto.setPostReportDtos(createPostReports(platformSummaryList, campaignId));
    return campaignSummaryDetailDto;
  }

  /**
   * <b>Create Post Report</b>
   * @param platformSummary
   * @param campaignId
   * @return	returns PostReportDto
   */
  private List<PostReportDto> createPostReports(List<PlatformReportDto> platformSummary,
      String campaignId) {

    if (CollectionUtils.isEmpty(platformSummary)) {
      return Collections.emptyList();
    }

    List<Post> postList = getPostsByCampaignId(campaignId);
    if (CollectionUtils.isEmpty(postList)) {
      return Collections.emptyList();
    }

    return postList.stream().map(post -> {
      return createPostReport(post);
    }).filter(d -> d != null).flatMap(postReportDtos -> postReportDtos.stream())
        .collect(Collectors.toList());
  }

  /**
   * <b>Get Post list by campaignId</b>
   * @param campaignId
   * @return	returns Post list
   */
  private List<Post> getPostsByCampaignId(String campaignId) {
    Filter filter = new Filter();
    filter.setName(ModelConstants.CAMPAIGN_ID);
    filter.setValues(Arrays.asList(campaignId));
    return postService.getAllPost(new PageRequest(0, 1000), Arrays.asList(filter));
  }

  /**
   * <b>Create Post Report</b>
   * @param post
   * @return	returns PostReportDto list
   */
  private List<PostReportDto> createPostReport(Post post) {
    if (post == null) {
      return null;
    }

    Aggregation aggregation =
        newAggregation(Aggregation.match(Criteria.where(POST_ID).is(post.getId())),
            Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
            Aggregation.group(PLATFORM, POST_ID, "socialPostId").first(LIKE_COUNT).as(LIKE_COUNT)
                .first("retweetCount").as(SHARE_COUNT).first(COMMENT_COUNT).as(COMMENT_COUNT)
                .first(POST_ID).as(POST_ID).first(PLATFORM).as(PLATFORM)/*,
            Aggregation.group(PLATFORM, POST_ID).sum(LIKE_COUNT).as(LIKE_COUNT).sum(SHARE_COUNT)
                .as(SHARE_COUNT).sum(COMMENT_COUNT).as(COMMENT_COUNT).first(POST_ID).as(POST_ID)
                .first(PLATFORM).as(PLATFORM)*/);
    final AggregationResults<PostReportDto> aggregationResult =
        mongoTemplate.aggregate(aggregation, CampaignSummary.class, PostReportDto.class);
    List<PostReportDto> postReportDtoList = aggregationResult.getMappedResults();
    if (CollectionUtils.isEmpty(postReportDtoList)) {
      return null;
    }

    for (PostReportDto postReportDto : postReportDtoList) {
    	postReportDto.setDescription(post.getText());
    	if(!CollectionUtils.isEmpty(post.getMediaIds())){	
    	Media media = mediaService.get(post.getMediaIds().get(0));
		if (!ObjectUtils.isEmpty(media)) {
			String url =urlUtil.getUrl(media.getClientId() + File.separator + media.getMediaType(),
					media.getHashFileName());
			postReportDto.setMediaUrl(url);
		}
    	}
    }
    return postReportDtoList;
  }

  /**
   * <b>Get PlateformSummary by campaignId and clientId</b>
   * @param campaignId
   * @param clientId
   * @return	returns PlatformReportDto list
   */
  private List<PlatformReportDto> getPlatformSummary(String campaignId, String clientId) {
    Aggregation aggregation =
        newAggregation(
            Aggregation.match(Criteria.where("campaignId").is(campaignId).and("clientId")
                .is(clientId)),
            Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
            Aggregation.group(PLATFORM, POST_ID, "socialPostId").first(LIKE_COUNT).as(LIKE_COUNT)
                .first("retweetCount").as(SHARE_COUNT).first(COMMENT_COUNT).as(COMMENT_COUNT)
                .first(POST_ID).as(POST_ID),
           /* Aggregation.group(PLATFORM, POST_ID).sum(LIKE_COUNT).as(LIKE_COUNT).sum(SHARE_COUNT)
                .as(SHARE_COUNT).sum(COMMENT_COUNT).as(COMMENT_COUNT).first(PLATFORM).as(PLATFORM)
                .count().as(POST_COUNT),*/
            Aggregation.group(PLATFORM).sum(LIKE_COUNT).as(LIKE_COUNT).sum(SHARE_COUNT)
                .as(SHARE_COUNT).sum(COMMENT_COUNT).as(COMMENT_COUNT).first(PLATFORM).as(PLATFORM)
                .count().as(POST_COUNT));
    final AggregationResults<PlatformReportDto> aggregationResult =
        mongoTemplate.aggregate(aggregation, CampaignSummary.class, PlatformReportDto.class);
    return aggregationResult.getMappedResults();
  }

  /**
   * <b> Get Top Post by plateformSumaary list and campaignId</b>
   * @param platformSummary
   * @param campaignId
   * @return	returns TopPostDto
   */
  private TopPostDto getTopPost(List<PlatformReportDto> platformSummary,
      String campaignId) {
    List<PostReportDto> postReports = createPostReports(platformSummary, campaignId); 
    if(CollectionUtils.isEmpty(postReports)){
      return null;
    }
    
    PostReportDto postReportDto =  postReports.stream().sorted(Comparator.comparing(PostReportDto::getLikeCount).reversed()).findFirst().get();
    TopPostDto topPostDto = new TopPostDto();
    topPostDto.setPost(postService.get(postReportDto.getPostId()));
    topPostDto.setPlatform(postReportDto.getPlatform());
    topPostDto.setPostId(postReportDto.getPostId());
    topPostDto.setLikeCount(postReportDto.getLikeCount());
    return topPostDto;
  }

  @Override
  public CampaignSummary update(String id, CampaignSummary entity) {
    return null;
  }

  @Override
  public CampaignSummary get(String id) {
    return null;
  }

  /**
   * <b>Validate Campaign Summary</b>
   * @param campaignSummary
   */
  private void validateCampaignSummary(CampaignSummary campaignSummary) {
    if (StringUtils.isEmpty(campaignSummary.getSocialPostId())) {
      log.info("Social post id cannot be null/empty");
      throw new IllegalArgumentException("Social post id cannot be null/empty");
    }
  }

  /**
   * <b>Get CampaignSummary by postId</b>
   * @param postId
   * @return		returns CampaignSummary
   */
  @Override
  public CampaignSummary getCampaignSummaryByPostId(@NotNull String postId) {
    try {
      return campaignSummaryDao.getByPostId(postId);
    } catch (Exception e) {
      String message = String.format("Error while fetching campaign summary by post id %s", postId);
      log.error(message);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get CampaignSummary by postId list</b>
   * @param postids	 list of postId	
   * @return		 returns campaignSummary list
   */
  @Override
  public List<CampaignSummary> getCampaignSummaryByPostIds(List<String> postids) {

    List<CampaignSummary> sampaignSummaries = new ArrayList<CampaignSummary>();
    try {
      postids.forEach(postId -> {
        CampaignSummary campaignSummary = campaignSummaryDao.getByPostId(postId);
        if (!ObjectUtils.isEmpty(campaignSummary)) {
          sampaignSummaries.add(campaignSummary);
        }
      });
      return sampaignSummaries;
    } catch (Exception e) {
      String message = "Error while fetching campaign summary by post ids";
      log.error(message);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get CampaignHashTagSummary list</b>
   * @param clientId
   * @param campaignId
   * @return	returns CampaignHashTagSummary list	
   */
  @Override
  public List<CampaignHashTagSummary> getCampaignHashTagSummaries(@NonNull String clientId,
      @NonNull String campaignId) {
    return null;
  }

}