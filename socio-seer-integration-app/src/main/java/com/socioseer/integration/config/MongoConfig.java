package com.socioseer.integration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

  @Value("${mongo.db.name}")
  private String mongoDBName;

  @Value("${mongo.host}")
  private String mongoHost;

  @Override
  protected String getDatabaseName() {
    return mongoDBName;
  }

  @Override
  public Mongo mongo() throws Exception {
    return new MongoClient(mongoHost);
  }

  @Bean
  public MongoTemplate mongoTemplate() throws Exception {
    return new MongoTemplate(mongoDbFactory());
  }

}
