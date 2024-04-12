package com.login.oauthAndJwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SessionCookieApplication {

	public static void main(String[] args) {
		SpringApplication.run(SessionCookieApplication.class, args);
	}

}
