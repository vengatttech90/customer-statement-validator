package com.rabobank.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabobank.test.stmtval.service.CsvFileExtractorServiceImplTest;
import com.rabobank.test.stmtval.service.StatementValidatorServiceImplTest;
import com.rabobank.test.stmtval.service.XmlFileExtractorServiceImplTest;

@RunWith(Suite.class)
@SuiteClasses({ XmlFileExtractorServiceImplTest.class, CsvFileExtractorServiceImplTest.class, StatementValidatorServiceImplTest.class })
public class RaboBankCustomerStatementValidatorApplicationTests {
	private static final Logger logger = LoggerFactory
			.getLogger(RaboBankCustomerStatementValidatorApplicationTests.class);
	 
}