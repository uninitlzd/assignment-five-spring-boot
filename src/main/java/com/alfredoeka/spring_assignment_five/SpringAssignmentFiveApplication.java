package com.alfredoeka.spring_assignment_five;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringAssignmentFiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAssignmentFiveApplication.class, args);
	}

}
