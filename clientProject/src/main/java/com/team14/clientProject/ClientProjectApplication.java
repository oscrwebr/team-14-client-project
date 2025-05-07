package com.team14.clientProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ClientProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientProjectApplication.class, args);
	}

}