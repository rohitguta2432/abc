package com.socioseer.restapp.dao.api;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.socioseer.common.domain.model.ClientFollowersInfo;

/**
 * <h3>ClientFollowersInfo Dao</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
public interface ClientFollowersInfoDao extends PagingAndSortingRepository<ClientFollowersInfo, String>{

}
