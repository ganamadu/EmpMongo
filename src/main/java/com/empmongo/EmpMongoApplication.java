package com.empmongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmpMongoApplication {
	private static final Logger logger = LoggerFactory.getLogger(EmpMongoApplication.class);


	public static void main(String[] args) {
		logger.info("EmpMongoApplication started....");
		SpringApplication.run(EmpMongoApplication.class, args);
	}

}
