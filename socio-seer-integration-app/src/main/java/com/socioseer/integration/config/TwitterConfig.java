package com.socioseer.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class TwitterConfig {

  @Value("${twitter.consumer.key}")
  private String consumerKey;

  @Value("${twitter.consumer.secret}")
  private String consumerSecret;

  public String getConsumerKey() {
    return consumerKey;
  }

  public String getConsumerSecret() {
    return consumerSecret;
  }
}
