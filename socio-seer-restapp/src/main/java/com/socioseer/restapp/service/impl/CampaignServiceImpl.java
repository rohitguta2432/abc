package com.socioseer.restapp.service.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableMap;
import com.socioseer.common.constants.ModelConstants;
import com.socioseer.common.constants.SocioSeerConstant;
import com.socioseer.common.domain.SocialHandler;
import com.socioseer.common.domain.Team;
import com.socioseer.common.domain.model.CampaignSchedulerDto;
import com.socioseer.common.domain.model.Client;
import com.socioseer.common.domain.model.campaign.Budget;
import com.socioseer.common.domain.model.campaign.Campaign;
import com.socioseer.common.domain.model.campaign.SocialPlatform;
import com.socioseer.common.domain.model.post.Post;
import com.socioseer.common.domain.model.post.PostSchedule;
import com.socioseer.common.dto.FBPostDto;
import com.socioseer.common.dto.Filter;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.cache.Cache;
import com.socioseer.restapp.dao.api.CampaignDao;
import com.socioseer.restapp.service.api.CampaignService;
import com.socioseer.restapp.service.api.ClientService;
import com.socioseer.restapp.service.api.PostService;
import com.socioseer.restapp.service.api.SocialHandlerService;
import com.socioseer.restapp.service.api.SocialPlatformService;
import com.socioseer.restapp.service.api.TeamService;
import com.socioseer.restapp.service.util.DateUtil;
import com.socioseer.restapp.service.util.FileUtility;
import com.socioseer.restapp.service.util.QueryBuilder;
import com.socioseer.restapp.util.UrlUtil;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * <h3>CampaignService Implementation</h3>
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Service
public class CampaignServiceImpl implements CampaignService {

  @Autowired
  private CampaignDao campaignDao;

  @Autowired
  private ClientService clientService;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private PostService postService;

  @Autowired
  private Cache cache;

  @Autowired
  private FileUtility fileUtility;

  @Autowired
  private UrlUtil urlUtil;

  @Autowired
  private SocialPlatformService socialPlatformService;

  @Autowired
  private SocialHandlerService socialHandlerService;

  @Autowired
  private TeamService teamService;

  private static final long ONE_DAY = 86400000;


  /**
   * <b>Update Campaign</b>
   * 
   * @param id
   * @param campaign
   * @param profilePicture
   * @return returns Campaign
   */
  @Override
  public Campaign update(@NonNull String id, @NonNull Campaign campaign,
      MultipartFile profilePicture) {
    String hashedFileName = null;
    try {
      Campaign campaignExisted = campaignDao.findOne(id);
      if (ObjectUtils.isEmpty(campaignExisted)) {
        String message = String.format("No campaign found with id : %s", id);
        log.info(message);
        throw new IllegalArgumentException(message);
      }
      if (profilePicture != null && !profilePicture.isEmpty() && !profilePicture
          .getOriginalFilename().equalsIgnoreCase(campaignExisted.getProfileimageName())) {
        fileUtility.deleteFile(SocioSeerConstant.CAMPAIGN_FOLDER, campaignExisted.getClientId(),
            campaignExisted.getHashedProfileImageName());
        hashedFileName = fileUtility.getFileName(profilePicture.getOriginalFilename());
        fileUtility.saveFile(profilePicture, SocioSeerConstant.CAMPAIGN_FOLDER,
            campaignExisted.getClientId(), hashedFileName);
        campaign.setProfileimageName(profilePicture.getOriginalFilename());
        campaign.setHashedProfileImageName(hashedFileName);
      } else {
        campaign.setProfileimageName(campaignExisted.getProfileimageName());
        campaign.setHashedProfileImageName(campaignExisted.getHashedProfileImageName());
      }
      campaign.setId(id);
      campaign.setCreatedDate(campaignExisted.getCreatedDate());
      campaign.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());
      campaign.setTitle(WordUtils.capitalizeFully(campaign.getTitle()));
      update(id, campaign);
      if (!ObjectUtils.isEmpty(campaignExisted)
          && !StringUtils.isEmpty(campaignExisted.getHashedProfileImageName()))
        campaignExisted.setProfileimageName(urlUtil.getUrl(
            SocioSeerConstant.CAMPAIGN_FOLDER + File.separator + campaignExisted.getClientId(),
            campaignExisted.getHashedProfileImageName()));
      return campaignExisted;
    } catch (Exception e) {
      fileUtility.deleteFile(campaign.getClientId(), SocioSeerConstant.CAMPAIGN_FOLDER,
          hashedFileName);
      throw e;
    }
  }

  /**
   * <b>Save Campaign</b>
   * 
   * @param campaign
   * @return returns Campaign
   */
  @Override
  public Campaign save(Campaign campaign) {
    Client client = clientService.get(campaign.getClientId());
    if (ObjectUtils.isEmpty(client)) {
      String message = String.format("Client not found by client id", campaign.getClientId());
      log.error(message);
      throw new SocioSeerException(message);
    }
    try {
      campaign.setCreatedDate(DateUtil.getCurrentTimeInMilliseconds());
      campaign.setUpdatedDate(campaign.getCreatedDate());
      Campaign savedCampaign = campaignDao.save(campaign);
      return savedCampaign;
    } catch (Exception e) {
      String message =
          String.format("Error while saving campaign for client id : %s", campaign.getClientId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Save Campaign</b>
   * 
   * @param campaign
   * @param profilePicture
   * @return returns Campaign
   */
  @Override
  public Campaign save(Campaign campaign, MultipartFile profilePicture) {
    validateCampaignDescription(campaign);
    Campaign existedCompaign =
        campaignDao.findByTitleAndClientId(campaign.getTitle(), campaign.getClientId());
    if (!ObjectUtils.isEmpty(existedCompaign)) {
      String message = String.format("Campaign already existed by title %s and client id %s",
          campaign.getTitle(), campaign.getClientId());
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    String hashedFileName = null;
    try {
      if (!ObjectUtils.isEmpty(profilePicture) && !profilePicture.isEmpty()) {
        hashedFileName = fileUtility.getFileName(profilePicture.getOriginalFilename());
        fileUtility.saveFile(profilePicture, SocioSeerConstant.CAMPAIGN_FOLDER,
            campaign.getClientId(), hashedFileName);
        campaign.setProfileimageName(profilePicture.getOriginalFilename());
        campaign.setHashedProfileImageName(hashedFileName);
      }
      campaign.setTitle(WordUtils.capitalizeFully(campaign.getTitle()));
      return save(campaign);
    } catch (Exception e) {
      fileUtility.deleteFile(campaign.getClientId(), SocioSeerConstant.CAMPAIGN_FOLDER,
          hashedFileName);
      throw e;
    }
  }

  /**
   * <b>Update Campaign</b>
   * 
   * @param id
   * @param campaign
   * @return returns Campaign
   */
  @Override
  public Campaign update(String id, Campaign campaign) {
    try {
      Campaign existingCampaign = campaignDao.findOne(id);
      if (ObjectUtils.isEmpty(existingCampaign)) {
        String message = String.format("No content found with id : %s", id);
        log.info(message);
        throw new IllegalArgumentException(message);
      }
      validateCampaignDetails(campaign);
      campaign.setId(existingCampaign.getId());
      // copyObject(campaign, existingCampaign);
      return campaignDao.save(campaign);
    } catch (Exception e) {
      String message = String.format("Error while updating campaign with id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get Campaign by id</b>
   * 
   * @param id
   * @return returns Campaign
   */
  @Override
  public Campaign get(String id) {
    Campaign campaign = campaignDao.findOne(id);
    if (!ObjectUtils.isEmpty(campaign)
        && !StringUtils.isEmpty(campaign.getHashedProfileImageName()))
      campaign.setHashedProfileImageName(urlUtil.getUrl(
          SocioSeerConstant.CAMPAIGN_FOLDER + File.separator + campaign.getClientId(),
          campaign.getHashedProfileImageName()));
    return campaign;
  }

  /**
   * <b>Delete Campaign by id</b>
   * 
   * @param id
   */
  @Override
  public void delete(String id) {
    Campaign campaign = campaignDao.findOne(id);
    if (ObjectUtils.isEmpty(campaign)) {
      String message = String.format("No content found with id : %s", id);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    campaignDao.delete(id);
  }

  /**
   * <b>Get All Campaign list</b>
   * 
   * @param pageable
   * @param filters
   * @return returns Campaign list
   */
  @Override
  public List<Campaign> getAllCampaigns(Pageable pageable, List<Filter> filters) {
    try {
      Query query = QueryBuilder.createQuery(filters, pageable);
      List<Campaign> campaigns = mongoTemplate.find(query, Campaign.class);
      getPostsByCampaigns(campaigns);
      appendUrl(campaigns);
      return campaigns;
    } catch (Exception e) {
      log.info("Error while fetching campaign", e);
      throw new SocioSeerException("Error while fetching campaign");
    }
  }

  /**
   * <b>Get Campaign list by clientId</b>
   * 
   * @param clientId
   * @param filters
   * @param pageable
   * @return returns Campaign list
   */
  @Override
  public List<Campaign> getAllCampaignsByClientId(@NonNull String clientId, List<Filter> filters,
      Pageable pageable) {
    Query createQuery = QueryBuilder.createQuery(filters,
        ImmutableMap.of(ModelConstants.CLIENT_ID, clientId), pageable);
    long currentDate = DateUtil.getCurrentTimeInMilliseconds();
    createQuery.addCriteria(new Criteria().andOperator(Criteria.where("endDate").gte(currentDate)));
    List<Campaign> campaigns = mongoTemplate.find(createQuery, Campaign.class);
    getPostsByCampaigns(campaigns);
    appendUrl(campaigns);
    return campaigns;
  }

  /**
   * <b>Get Campaign list by clientId</b>
   * 
   * @param clientId
   * @return returns Campaign list
   */
  @Override
  public List<Campaign> getAllCampaignsByClientId(String clientId) {
    Query createQuery = new Query(Criteria.where("clientId").is(clientId));
    List<Campaign> campaigns = mongoTemplate.find(createQuery, Campaign.class);
    appendUrl(campaigns);
    return campaigns;
  }

  /**
   * <b>Get Campaign by userId</b>
   * 
   * @param userId
   * @param filters
   * @param pageable
   * @return returns Campaign list
   */
  @Override
  public List<Campaign> getAllCampaignsByUserId(@NonNull String userId, List<Filter> filters,
      Pageable pageable) {
    Query query = QueryBuilder.createQuery(filters, ImmutableMap.of(ModelConstants.USER_ID, userId),
        pageable);
    List<Campaign> campaigns = mongoTemplate.find(query, Campaign.class);
    appendUrl(campaigns);
    return campaigns;
  }

  /**
   * <b>Get Active Campaign HashTag</b>
   * 
   * @param currentTime
   * @return returns map object
   */
  @Override
  public Map<String, List<String>> getActiveCampaignHashTags(long currentTime) {

    Query query = new Query();
    query.addCriteria(Criteria.where(ModelConstants.END_DATE).gte(currentTime));
    query.fields().include(ModelConstants.ID);
    query.fields().include(ModelConstants.HASH_TAGS);
    List<Campaign> campaigns = mongoTemplate.find(query, Campaign.class);
    if (CollectionUtils.isEmpty(campaigns)) {
      return Collections.emptyMap();
    }
    try {
      return campaigns.stream().collect(Collectors.toMap(Campaign::getId, Campaign::getHashtags));
    } catch (Exception e) {
      log.info("Error while fetching active campaign hashtags", e);
      throw new SocioSeerException("Error while fetching active campaign hashtags");
    }
  }

  /**
   * Validate campaign description.
   *
   * @param campaign the campaign
   */
  private void validateCampaignDescription(Campaign campaign) {

    if (StringUtils.isBlank(campaign.getClientId())) {
      log.info("Client id can not be empty/null");
      throw new IllegalArgumentException("Client id can not be empty/null");
    }

    if (StringUtils.isBlank(campaign.getCreatedBy())) {
      log.info("User id can not be empty/null");
      throw new IllegalArgumentException("User id can not be empty/null");
    }

    if (StringUtils.isBlank(campaign.getTitle())) {
      log.info("Title can not be empty/null");
      throw new IllegalArgumentException("Title can not be empty/null");
    }

    if (StringUtils.isBlank(campaign.getObjective())) {
      log.info("Objective can not be empty/null");
      throw new IllegalArgumentException("Objective can not be empty/null");
    }

    if (StringUtils.isBlank(campaign.getDescription())) {
      log.info("Description can not be empty/null");
      throw new IllegalArgumentException("Description can not be empty/null");
    }

    if (ObjectUtils.isEmpty(campaign.getLocation())) {
      log.info("Location can not be null");
      throw new IllegalArgumentException("Location can not be null");
    }

    if (campaign.getStartDate() == 0) {
      log.info("Start date can not be null");
      throw new IllegalArgumentException("Start date can not be null");
    }

    if (campaign.getEndDate() == 0) {
      log.info("Campaign end date can not be null");
      throw new IllegalArgumentException("End date can not be null");
    }

    if (StringUtils.isEmpty(campaign.getCreatedBy())) {
      log.info("User id name can not be empty/null");
      throw new IllegalArgumentException("created by can not be empty/null");
    }

    if (ObjectUtils.isEmpty(campaign.getTeam())) {
      log.info("No teams associated with cmapaign.");
      throw new IllegalArgumentException("No teams associated with cmapaign.");
    }

  }

  /**
   * Validate campaign details.
   *
   * @param campaign the campaign
   */
  private void validateCampaignDetails(Campaign campaign) {
    if (CollectionUtils.isEmpty(campaign.getKeywords())) {
      log.info("Keywords List can not be empty/null");
      throw new IllegalArgumentException("Keywords List can not be empty/null");
    }

    if (CollectionUtils.isEmpty(campaign.getHashtags())) {
      log.info("HashTags List can not be empty/null");
      throw new IllegalArgumentException("HashTags List can not be empty/null");
    }

    if (ObjectUtils.isEmpty(campaign.getTeam())) {
      log.info("Team List can not be empty/null");
      throw new IllegalArgumentException("Team List can not be empty/null");
    }

    if (StringUtils.isEmpty(campaign.getUpdatedBy())) {
      log.info("updated by can not be empty/null");
      throw new IllegalArgumentException("updated by can not be empty/null");
    }
  }

  /**
   * 
   * @param campaign
   * @param existingCampaign
   */
  private void copyObject(Campaign campaign, Campaign existingCampaign) {

    if (campaign.getClientId() != null) {
      Client client = clientService.get(campaign.getClientId());
      if (ObjectUtils.isEmpty(client)) {
        String message = String.format("Client is not found by id %s", campaign.getClientId());
        log.info(message);
        throw new IllegalArgumentException(message);
      }
      existingCampaign.setClientId(campaign.getClientId());
    }

    if (!StringUtils.isEmpty(campaign.getProfileimageName())) {
      existingCampaign.setProfileimageName(campaign.getProfileimageName());
    }

    if (!StringUtils.isEmpty(campaign.getHashedProfileImageName())) {
      existingCampaign.setHashedProfileImageName(campaign.getHashedProfileImageName());
    }

    if (!StringUtils.isEmpty(campaign.getCreatedBy()))
      existingCampaign.setCreatedBy(campaign.getCreatedBy());

    if (!StringUtils.isEmpty(campaign.getDescription()))
      existingCampaign.setDescription(campaign.getDescription());

    if (campaign.getEndDate() > 0)
      existingCampaign.setEndDate(campaign.getEndDate());

    if (!StringUtils.isEmpty(campaign.getAuthor()))
      existingCampaign.setAuthor(campaign.getAuthor());

    if (!CollectionUtils.isEmpty(campaign.getHashtags()))
      existingCampaign.setHashtags(campaign.getHashtags());

    if (!CollectionUtils.isEmpty(campaign.getKeywords()))
      existingCampaign.setKeywords(campaign.getKeywords());

    if (!CollectionUtils.isEmpty(campaign.getTargetAudience()))
      existingCampaign.setTargetAudience(campaign.getTargetAudience());

    if (!ObjectUtils.isEmpty(campaign.getLocation()))
      existingCampaign.setLocation(campaign.getLocation());

    if (!StringUtils.isEmpty(campaign.getObjective()))
      existingCampaign.setObjective(campaign.getObjective());

    if (!StringUtils.isEmpty(campaign.getTitle()))
      existingCampaign.setTitle(campaign.getTitle());

    if (!StringUtils.isEmpty(campaign.getUpdatedBy()))
      existingCampaign.setUpdatedBy(campaign.getUpdatedBy());

    existingCampaign.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());

    if (!CollectionUtils.isEmpty(campaign.getPlatformList())) {
      List<SocialPlatform> platformList = new ArrayList<SocialPlatform>();

      campaign.getPlatformList().forEach(platform -> {
        SocialPlatform socialPlatform = new SocialPlatform();

        if (!StringUtils.isEmpty(platform.getId()))
          socialPlatform.setId(platform.getId());

        if (!StringUtils.isEmpty(platform.getCreatedBy()))
          socialPlatform.setCreatedBy(platform.getCreatedBy());

        if (!StringUtils.isEmpty(platform.getName()))
          socialPlatform.setName(platform.getName());

        if (!StringUtils.isEmpty(platform.getUpdatedBy()))
          socialPlatform.setUpdatedBy(platform.getUpdatedBy());

        socialPlatform.setUpdatedDate(DateUtil.getCurrentTimeInMilliseconds());

        platformList.add(socialPlatform);

      });
      existingCampaign.setPlatformList(platformList);
    }

    if (!CollectionUtils.isEmpty(campaign.getBudgetList())) {

      List<Budget> budgetList = new ArrayList<Budget>();

      campaign.getBudgetList().forEach(budget -> {
        Budget bud = new Budget();

        if (budget.getBudget().longValue() > 0)
          bud.setBudget(budget.getBudget());

        if (!StringUtils.isEmpty(budget.getPlatform()))
          bud.setPlatform(budget.getPlatform());

        if (budget.getStartDate() > 0)
          bud.setStartDate(budget.getStartDate());

        if (budget.getEndDate() > 0)
          bud.setEndDate(budget.getEndDate());

        if (!StringUtils.isEmpty(budget.getCurrency())) {
          bud.setCurrency(budget.getCurrency());
        }

        if (!StringUtils.isEmpty(budget.getPlatformId())) {
          bud.setPlatformId(budget.getPlatformId());
        }

        if (!StringUtils.isEmpty(budget.getDuration())) {
          bud.setDuration(budget.getDuration());
        }
        budgetList.add(bud);
      });
      existingCampaign.setBudgetList(budgetList);
    }

    if (!ObjectUtils.isEmpty(campaign.getTeam())) {
      existingCampaign.setTeam(campaign.getTeam());
    }
    copyBrandAndhandlesObject(campaign, existingCampaign);
    copyPlatForm(campaign, existingCampaign);
    copyPostMetricsObject(campaign, existingCampaign);

  }

  /**
   * 
   * @param campaign
   * @param existingCampaign
   */
  private void copyBrandAndhandlesObject(Campaign campaign, Campaign existingCampaign) {

    if (!CollectionUtils.isEmpty(campaign.getBrands())) {
      existingCampaign.setBrands(campaign.getBrands());
    }
    if (!CollectionUtils.isEmpty(campaign.getHandles())) {
      existingCampaign.setHandles(campaign.getHandles());
    }

  }

  /**
   * 
   * @param campaign
   * @param existingCampaign
   */
  private void copyPlatForm(Campaign campaign, Campaign existingCampaign) {

    if (!CollectionUtils.isEmpty(campaign.getPlatformList())) {
      existingCampaign.setPlatformList(campaign.getPlatformList());
    }
  }

  /**
   * 
   * @param campaign
   * @param existingCampaign
   */
  private void copyPostMetricsObject(Campaign campaign, Campaign existingCampaign) {
    if (!CollectionUtils.isEmpty(campaign.getPostMetrics())) {
      existingCampaign.setPostMetrics(campaign.getPostMetrics());
    }

  }

  /**
   * <b>Change Campaign Status by id</b>
   * 
   * @param id
   * @param status
   * @param updatedBy
   */
  @Override
  public void changeStatus(String id, int status, String updatedBy) {
    if (status < 0) {
      String message = String.format("Invalid status %s", status);
      log.info(message);
      throw new IllegalArgumentException(message);
    }
    try {
      Campaign campaign = campaignDao.findOne(id);
      if (ObjectUtils.isEmpty(campaign)) {
        String message = String.format("Compaign not found by id %s", id);
        log.info(message);
        throw new IllegalArgumentException(message);
      }
      campaign.setStatus(status);
      campaign.setUpdatedBy(updatedBy);
      campaignDao.save(campaign);
    } catch (Exception e) {
      String message = String.format("Error while fetching compaign by id : %s", id);
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get Count of Campaign</b>
   * 
   * @return returns integer
   */
  @Override
  public int countAll() {
    try {
      return (int) campaignDao.count();
    } catch (Exception e) {
      String message = String.format("Error while count all compaign");
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  /**
   * <b>Get Post By Campaigns</b>
   * 
   * @param campaigns
   */
  private void getPostsByCampaigns(List<Campaign> campaigns) {
    campaigns.forEach(campaign -> {
      List<Post> filterList = postService.findAllByCampaignId(campaign.getId());
      if (!CollectionUtils.isEmpty(filterList)) {
        campaign.setCountPost(filterList.size());
      }
    });
  }

  /**
   * <b>Get CampaignSchedulerDto list by clientId</b>
   * 
   * @param clientId
   * @param filters
   * @param pageable
   * @param startDate long data
   * @param endDate long data
   * @return returns map object
   */
  @Override
  public Map<Long, List<CampaignSchedulerDto>> filterCampaignScheduleByClientId(
      @NonNull String clientId, List<Filter> filters, Pageable pageable, Long startDate,
      Long endDate) {
    try {
      Query createQuery = QueryBuilder.createQuery(filters,
          ImmutableMap.of(ModelConstants.CLIENT_ID, clientId), pageable);
      List<Campaign> campaigns = mongoTemplate.find(createQuery, Campaign.class);
      return filterCampaignSchedule(campaigns, startDate, endDate);
    } catch (Exception e) {
      log.info("Error while fetching campaign", e);
      throw new SocioSeerException("Error while fetching campaign");
    }
  }

  /**
   * 
   * @param campaigns
   * @param startDate
   * @param endDate
   * @return returns map object
   */
  private Map<Long, List<CampaignSchedulerDto>> filterCampaignSchedule(List<Campaign> campaigns,
      Long startDate, Long endDate) {
    Map<Long, List<CampaignSchedulerDto>> campaignSchedulers =
        new HashMap<Long, List<CampaignSchedulerDto>>();
    for (long d = startDate; d <= endDate; d += ONE_DAY) {
      List<CampaignSchedulerDto> campaignSchedulerDtos = new ArrayList<CampaignSchedulerDto>();
      for (Campaign campaign : campaigns) {
        if (campaign.getStartDate() >= d && campaign.getStartDate() < (d + ONE_DAY)) {
          CampaignSchedulerDto campaignSchedulerDto = new CampaignSchedulerDto();
          campaignSchedulerDto.setId(campaign.getId());
          campaignSchedulerDto.setName(campaign.getTitle());
          campaignSchedulerDto.setDescription(campaign.getDescription());
          List<String> socialPlateForm = new ArrayList<String>();
          if (!CollectionUtils.isEmpty(campaign.getPlatformList())) {
            campaign.getPlatformList().forEach(socialPlatform -> {
              socialPlateForm.add(socialPlatform.getName());
            });
            campaignSchedulerDto.setSocialPlatformNames(socialPlateForm);
            campaignSchedulerDtos.add(campaignSchedulerDto);
          }
        }
        if (!CollectionUtils.isEmpty(campaignSchedulerDtos)) {
          campaignSchedulers.put(d, campaignSchedulerDtos);
        }
      }
    }
    return campaignSchedulers;
  }

  /**
   * 
   * @param campaigns
   */
  private void appendUrl(List<Campaign> campaigns) {
    if (!CollectionUtils.isEmpty(campaigns)) {
      campaigns.forEach(campaign -> {
        if (!ObjectUtils.isEmpty(campaign)
            && (!StringUtils.isEmpty(campaign.getHashedProfileImageName())))
          if (!ObjectUtils.isEmpty(campaign)
              && !StringUtils.isEmpty(campaign.getHashedProfileImageName()))
            campaign.setHashedProfileImageName(urlUtil.getUrl(
                SocioSeerConstant.CAMPAIGN_FOLDER + File.separator + campaign.getClientId(),
                campaign.getHashedProfileImageName()));
      });
    }
  }

  /**
   * @param currentTime
   */
  @Override
  public void putActiveCampaignFBPostToCache(long currentTime) {
    List<Campaign> campaignList =
        campaignDao.findByEndDateGreaterThanEqualAndStartDateLessThan(currentTime, currentTime);
    if (CollectionUtils.isEmpty(campaignList)) {
      return;
    }
    putCampaignDataIntoCache(campaignList);
  }

  @Override
  public void putCampaignIntoCacheOnApplicationStartup() {
    Iterable<Campaign> findAll = campaignDao.findAll();
    List<Campaign> campaignList = new ArrayList<>();
    findAll.forEach(c -> campaignList.add(c));
    if (CollectionUtils.isEmpty(campaignList)) {
      return;
    }
    putCampaignDataIntoCache(campaignList);

  }


  /**
   * @param campaignList
   */
  @Override
  public void putCampaignDataIntoCache(List<Campaign> campaignList) {
    SocialPlatform fbSocialPlatform = socialPlatformService
        .getPlatformByName(ModelConstants.SOCIAL_PLATFORM_FACEBOOK.toLowerCase());
    SocialPlatform twitterSocialPlatform = socialPlatformService
        .getPlatformByName(ModelConstants.SOCIAL_PLATFORM_TWITTER.toLowerCase());

    if (fbSocialPlatform == null || twitterSocialPlatform == null) {
      return;
    }
    List<String> socialHandlerIds = new ArrayList<>();
    socialHandlerIds.addAll(getSocialHandlers(fbSocialPlatform));
    socialHandlerIds.addAll(getSocialHandlers(twitterSocialPlatform));

    if (CollectionUtils.isEmpty(socialHandlerIds)) {
      return;
    }

    List<String> campaignIds =
        campaignList.stream().map(campaign -> campaign.getId()).collect(Collectors.toList());
    Aggregation aggregation = newAggregation(
        match(Criteria.where("campaignId").in(campaignIds).and("isExecuted").is(true)
            .and("postHandlers.handlerId").in(socialHandlerIds)),
        Aggregation.unwind("postHandlers"), Aggregation.unwind("postHandlers.socialPostIds"),
        group("postHandlers.handlerId", "postHandlers.socialPostIds").first("post.$id").as("postId")
            .first("campaignId").as("campaignId").first("postHandlers.socialPostIds")
            .as("socialPostId").first("postHandlers.handlerId").as("handlerId").first("id")
            .as("postScheduleId"));

    final AggregationResults<FBPostDto> aggregationResult =
        mongoTemplate.aggregate(aggregation, PostSchedule.class, FBPostDto.class);

    List<FBPostDto> postDTOList = aggregationResult.getMappedResults();
    if (CollectionUtils.isEmpty(postDTOList)) {
      return;
    }

    Map<String, List<FBPostDto>> socialHandlerDtoMap =
        postDTOList.stream().filter(fbPostDto -> fbPostDto.getSocialPostId() != null)
            .collect(Collectors.groupingBy(FBPostDto::getHandlerId));

    cache.delete(FBPostDto.CACHE_OBJECT);
    cache.multiPut(FBPostDto.CACHE_OBJECT, socialHandlerDtoMap);
  }

  /**
   * 
   * @param socialPlatform
   * @return returns string list
   */
  private List<String> getSocialHandlers(SocialPlatform socialPlatform) {
    List<SocialHandler> socialHandlers = socialHandlerService.findBySocialPlatform(socialPlatform);
    if (CollectionUtils.isEmpty(socialHandlers)) {
      return Collections.emptyList();
    }

    List<String> socialHandlerIds =
        socialHandlers.stream().map(SocialHandler::getId).collect(Collectors.toList());
    return socialHandlerIds;
  }


  /**
   * <b>Get Campaign list by userId and team</b>
   * 
   * @param userId
   * @param filters
   * @param pageable
   * @return returns Campaign list
   */
  @Override
  public List<Campaign> getAllCampaignsByUserIdAndTeam(String userId, List<Filter> filters,
      Pageable pageable) {
    Criteria criteria = new Criteria();
    criteria.orOperator(Criteria.where(ModelConstants.USERS_LIST).is(userId),
        Criteria.where(ModelConstants.APPROVER_NAME).is(userId));
    Query query = new Query(criteria);
    List<Team> teams = mongoTemplate.find(query, Team.class);
    List<Campaign> campaigns = new ArrayList<Campaign>();
    if (!CollectionUtils.isEmpty(teams)) {
      teams.forEach(team -> {
        List<Campaign> campaignsFind = campaignDao.findByTeam(team);
        if (!CollectionUtils.isEmpty(campaignsFind)) {
          campaigns.addAll(campaignsFind);
        }
      });
    }
    getPostsByCampaigns(campaigns);
    return campaigns;

  }


}
