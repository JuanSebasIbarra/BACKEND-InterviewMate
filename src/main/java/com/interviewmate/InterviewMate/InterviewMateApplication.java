package com.interviewmate.InterviewMate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InterviewMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewMateApplication.class, args);
		System.out.println("================================================================");
		System.out.println("================================================================");
		System.out.println("             THE SERVER IS RUNNING SUCCESSFULLY");
		System.out.println("================================================================");
		System.out.println("================================================================");
	}

}
