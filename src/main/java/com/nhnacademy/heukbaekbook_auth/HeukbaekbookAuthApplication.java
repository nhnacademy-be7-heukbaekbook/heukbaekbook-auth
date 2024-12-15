package com.nhnacademy.heukbaekbook_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HeukbaekbookAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeukbaekbookAuthApplication.class, args);
	}

}
