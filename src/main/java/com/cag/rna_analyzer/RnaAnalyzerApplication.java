package com.cag.rna_analyzer;

import com.cag.rna_analyzer.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RnaAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RnaAnalyzerApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext context){
		return args -> {
			UserService userService = context.getBean(UserService.class);
			userService.getUser();
		};
	}
}