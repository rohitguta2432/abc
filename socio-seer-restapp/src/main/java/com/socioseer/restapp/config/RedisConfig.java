package com.socioseer.restapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Configuration
public class RedisConfig {

  @Value("${redis.host}")
  private String redisHostName;

  @Value("${redis.port}")
  private int redisPort;

  @Value("${redis.password}")
  private String redisPassword;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
/**
 * 
 * @return returns JedisConnectionFactory
 */
  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    JedisConnectionFactory factory = new JedisConnectionFactory();
    factory.setHostName(redisHostName);
    factory.setPort(redisPort);
    factory.setPassword(redisPassword);
    factory.setUsePool(true);
    factory.afterPropertiesSet();
    return factory;
  }

  /**
   * 
   * @return returns RedisTemplate
   */
  @Bean
  RedisTemplate<?, ?> redisTemplate() {
    RedisTemplate<?, ?> redisTemplate = new RedisTemplate<Object, Object>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());
    return redisTemplate;
  }

  /**
   * 
   * @return returns RedisCacheManager
   */
  @Bean
  RedisCacheManager cacheManager() {
    RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
    return redisCacheManager;
  }
}
