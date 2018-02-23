package com.socioseer.acl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class SocioSeerAuthorizationApp {

	public static void main(String[] args) {
		SpringApplication.run(SocioSeerAuthorizationApp.class, args);
	}

}
