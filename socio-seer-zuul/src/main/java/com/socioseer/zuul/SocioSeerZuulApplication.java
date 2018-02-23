package com.socioseer.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * 
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@EnableFeignClients
public class SocioSeerZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocioSeerZuulApplication.class, args);
	}
}
