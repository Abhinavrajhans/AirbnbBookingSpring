package com.example.AirbnbBookingSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AirbnbBookingSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirbnbBookingSpringApplication.class, args);
	}

}
