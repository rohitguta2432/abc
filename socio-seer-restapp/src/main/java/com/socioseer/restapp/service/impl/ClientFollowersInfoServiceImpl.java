package com.socioseer.restapp.service.impl;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.socioseer.common.domain.model.ClientFollowersInfo;
import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.restapp.dao.api.ClientFollowersInfoDao;
import com.socioseer.restapp.service.api.ClientFollowersInfoService;

/**
 * <h3>ClientFollowersInfoService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Component
@Slf4j
public class ClientFollowersInfoServiceImpl implements ClientFollowersInfoService {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private ClientFollowersInfoDao clientFollowersInfoDao;

  /**
   * <b>Save ClientFollowersInfo</b>
   * @param	clientFollowersInfo
   * @return returns ClientFollowersInfo
   */
  @Override
  public ClientFollowersInfo save(@NonNull ClientFollowersInfo clientFollowersInfo) {
    validate(clientFollowersInfo);
    clientFollowersInfo.setCreatedDate(System.currentTimeMillis());
    try {
      ClientFollowersInfo existing =
          findByClientIdAndPlatformAndHandlerId(clientFollowersInfo.getClientId(),
              clientFollowersInfo.getPlatform(), clientFollowersInfo.getHandlerId());
      double followersChange = 0;
      if (existing != null && existing.getFollowersCount() > 0) {
        followersChange =((double)(clientFollowersInfo.getFollowersCount() - existing.getFollowersCount()) / existing.getFollowersCount()) * 100;       
      }
      
      if(existing == null){
    	  clientFollowersInfo.setFollowersChange(0);
      }else{
    	  if(existing.getFollowersCount() != clientFollowersInfo.getFollowersCount()){
        	  clientFollowersInfo.setFollowersChange(followersChange);
          }else{
        	  clientFollowersInfo.setFollowersChange(existing.getFollowersChange());  
          }
      }
      
      return clientFollowersInfoDao.save(clientFollowersInfo);
    } catch (Exception e) {
      String message =
          String.format("Error while saving client followers info for client id : %s",
              clientFollowersInfo.getClientId());
      log.error(message, e);
      throw new SocioSeerException(message);
    }
  }

  @Override
  public ClientFollowersInfo update(String id, ClientFollowersInfo entity) {
    return null;
  }

  /**
   * <b>Get ClientFollowersInfo by id</b>
   * @param		id
   * @return	returns ClientFollowersInfo
   */
  @Override
  public ClientFollowersInfo get(String id) {
    return clientFollowersInfoDao.findOne(id);
  }

  /**
	 * <b>Get ClientFollowersInfo by clientId and platformId and handlerId</b>
	 * @param clientId
	 * @param platformId
	 * @param handlerId
	 * @return	returns ClientFollowersInfo
	 */
  @Override
  public ClientFollowersInfo findByClientIdAndPlatformAndHandlerId(String clientId,
      String platformId, String handlerId) {
    Query query =
        new Query(Criteria.where("clientId").is(clientId).and("platform").is(platformId)
            .and("handlerId").is(handlerId));
    query.with(new Sort(Direction.DESC, "createdDate"));
    return mongoTemplate.findOne(query, ClientFollowersInfo.class);
  }

  /**
   * <b>Get ClientFollowersInfo list by clientId</b>
   * @param clientId
   * @return	returns ClientFollowersInfo list	
   */
  @Override
  public List<ClientFollowersInfo> aggregateByClientId(String clientId) {
    Aggregation aggregation =
        newAggregation(
            Aggregation.match(Criteria.where("clientId").is(clientId)),
            Aggregation.sort(new Sort(Direction.DESC, "createdDate")),
            Aggregation.group("clientId", "platform", "handlerId").first("followersChange")
                .as("followersChange").first("platform").as("platform").first("handlerId")
                .as("handlerId").first("clientId").as("clientId").first("followersCount")
                .as("followersCount"),
            Aggregation.group("clientId", "platform").sum("followersChange").as("followersChange")
                .first("platform").as("platform").first("clientId").as("clientId")
                .sum("followersCount").as("followersCount"));
    final AggregationResults<ClientFollowersInfo> aggregationResult =
        mongoTemplate.aggregate(aggregation, ClientFollowersInfo.class, ClientFollowersInfo.class);
    return aggregationResult.getMappedResults();
  }

  /**
   * <b>Validate ClientFollowersInfo</b>
   * @param clientFollowersInfo
   */
  private void validate(ClientFollowersInfo clientFollowersInfo) {
    if (StringUtils.isEmpty(clientFollowersInfo.getClientId())) {
      throw new IllegalArgumentException("client id cannot be null or empty");
    }

    if (StringUtils.isEmpty(clientFollowersInfo.getHandlerId())) {
      throw new IllegalArgumentException("handler id cannot be null or empty");
    }

    if (StringUtils.isEmpty(clientFollowersInfo.getPlatform())) {
      throw new IllegalArgumentException("platform id cannot be null or empty");
    }
  }


}
