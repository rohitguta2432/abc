package com.socioseer.zuul.config;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.socioseer.common.exception.SocioSeerException;
import com.socioseer.zuul.filter.SocioSeerApiFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Configuration
public class ApplicationConfig {

  @Value("#{'${socio.seer.apis}'.split(',')}")
  private List<String> apiList;

  @Value("${socio.seer.auth.header}")
  private String authHeaderToken;

  @Value("${socio.seer.secret.key}")
  private String secretKey;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private AuthorizationService authorizationService;

  @Bean
  @LoadBalanced
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public SocioSeerApiFilter socioSeerApiFilter() {
    return new SocioSeerApiFilter(apiList, authorizationService, authenticationService,
        authHeaderToken, secretKey);
  }

  /**
   *  
   * @return returns MultipartResolver
   */
  @Bean
  public MultipartResolver multipartResolver() {
    return new StandardServletMultipartResolver() {
      @Override
      public boolean isMultipart(HttpServletRequest request) {
        String method = request.getMethod().toLowerCase();
        if (!Arrays.asList("put", "post").contains(method)) {
          return false;
        }
        String contentType = request.getContentType();
        return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
      }
    };
  }

  /**
   * 
   * @return returns TomcatEmbeddedServletContainerFactory
   */
  @Bean
  public TomcatEmbeddedServletContainerFactory containerFactory() {
	  TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
	  try{
		  
       factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
    	   
          @Override
          public void customize(Connector connector) {
           ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
          }
          
       });
	  }catch(Exception e){
		  log.error("Media is not more then 15 Mb");
		  throw new SocioSeerException("Media is not more then 15 Mb" ,e);
	  }
       return factory;
  }
  
}
