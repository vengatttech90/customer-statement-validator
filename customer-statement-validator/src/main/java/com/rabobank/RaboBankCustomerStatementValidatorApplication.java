package com.rabobank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.rabobank.*"})
public class RaboBankCustomerStatementValidatorApplication {
	private static final Logger logger = LoggerFactory.getLogger(RaboBankCustomerStatementValidatorApplication.class);

	public static void main(String[] args) {
		logger.info("executing main thread");
		SpringApplication.run(RaboBankCustomerStatementValidatorApplication.class, args);
	}
}