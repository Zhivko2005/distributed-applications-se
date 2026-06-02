package com.freelance.freelance_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FreelanceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreelanceApiApplication.class, args);
	}

}
