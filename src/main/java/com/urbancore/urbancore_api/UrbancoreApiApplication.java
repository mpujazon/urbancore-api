package com.urbancore.urbancore_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class UrbancoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrbancoreApiApplication.class, args);
	}

}
