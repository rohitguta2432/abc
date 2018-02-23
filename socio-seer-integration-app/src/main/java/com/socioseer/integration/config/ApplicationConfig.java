package com.socioseer.integration.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.socioseer.common.exception.SocioSeerException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@Configuration
public class ApplicationConfig extends WebMvcConfigurerAdapter {


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
