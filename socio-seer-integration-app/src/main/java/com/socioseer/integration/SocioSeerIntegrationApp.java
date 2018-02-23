package com.socioseer.integration;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @author  OrangeMantra
 * @since   JDK 1.8
 * @version 1.0
 *
 */
@Slf4j
@SpringBootApplication
public class SocioSeerIntegrationApp {
  
	
	public static void main(String[] args) {
    log.info("Starting integration app.");
    SpringApplication.run(SocioSeerIntegrationApp.class, args);
    log.info("Shutting down integration app.");
  }
}
