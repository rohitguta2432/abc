package com.socioseer.restapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;

import com.socioseer.restapp.config.QuartzConfig;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@SpringBootApplication
@EnableEurekaClient
@Import({QuartzConfig.class})
public class SocioSeerRestApplication {
  public static void main(String[] args) {
    SpringApplication.run(SocioSeerRestApplication.class, args);
  }
}
