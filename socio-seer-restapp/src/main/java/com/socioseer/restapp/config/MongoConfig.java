package com.socioseer.restapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.socioseer.restapp.dao.RepositoryPackage;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Configuration
@EnableMongoRepositories(basePackageClasses = RepositoryPackage.class)
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
  protected String getMappingBasePackage() {
    return "com.socioseer.common.domain";
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
