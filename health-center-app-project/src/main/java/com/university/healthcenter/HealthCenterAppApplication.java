package com.university.healthcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HealthCenterAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthCenterAppApplication.class, args);
	}

}
