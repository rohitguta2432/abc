package com.socioseer.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@SpringBootApplication
@EnableEurekaClient
public class SocioSeerAuthServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(SocioSeerAuthServiceApp.class, args);
	}

}
