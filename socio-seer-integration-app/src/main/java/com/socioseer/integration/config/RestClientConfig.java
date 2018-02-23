package com.socioseer.integration.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {


  @Value("${rest.client.connectionTimeoutMillis}")
  private int restClientConnectionTimeoutMillis;

  @Value("${rest.client.readTimeoutMillis}")
  private int restClientReadTimeoutMillis;

  @Value("${rest.client.maxConnectionsPerHost}")
  private int restClientMaxConnectionsPerHost;

  @Value("${rest.client.maxTotalConnections}")
  private int restClientMaxTotalConnections;


  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public HttpClient httpClient() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(restClientMaxTotalConnections);
    connectionManager.setDefaultMaxPerRoute(restClientMaxConnectionsPerHost);
    return HttpClients.custom().setConnectionManager(connectionManager).build();
  }


  @Bean
  public ClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory(httpClient());
    factory.setConnectTimeout(restClientConnectionTimeoutMillis);
    factory.setReadTimeout(restClientReadTimeoutMillis);
    return factory;
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
    return restTemplate;
  }

}
