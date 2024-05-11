package com.valuelabs.livequiz;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EntityScan(basePackages = "com.valuelabs.livequiz.model.entity")
@ComponentScan(basePackages = "com.valuelabs.livequiz")
public class LiveQuizApplication {
	public static void main(String[] args) {
		SpringApplication.run(LiveQuizApplication.class, args);
	}
}
