package com.socioseer.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SocioSeerEurekaApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SocioSeerEurekaApplication.class, args);
	}
}
