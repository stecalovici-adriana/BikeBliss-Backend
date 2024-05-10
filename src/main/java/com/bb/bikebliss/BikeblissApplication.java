package com.bb.bikebliss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BikeblissApplication {

	public static void main(String[] args) {

		SpringApplication.run(BikeblissApplication.class, args);
	}

}
