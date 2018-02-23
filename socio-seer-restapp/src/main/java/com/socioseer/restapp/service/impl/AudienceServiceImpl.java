package com.socioseer.restapp.service.impl;


import org.springframework.stereotype.Service;

import com.socioseer.common.domain.model.Audience;
import com.socioseer.restapp.service.api.AudienceService;

/**
 * <h3>AudienceService Implementation</h3>
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Service
public class AudienceServiceImpl implements AudienceService{

  
  
  @Override
  public Audience save(Audience entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Audience update(String id, Audience entity) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Audience get(String id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isExists(String audienceId) {
    // TODO Auto-generated method stub
    return true;
  }
}
