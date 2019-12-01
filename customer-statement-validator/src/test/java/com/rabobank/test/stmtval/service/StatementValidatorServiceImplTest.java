package com.rabobank.test.stmtval.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import com.rabobank.stmtval.model.Statement;
import com.rabobank.stmtval.model.ValidatedStatement;
import com.rabobank.stmtval.service.StatementValidatorService;
import com.rabobank.stmtval.serviceimpl.CsvFileExtractorServiceImpl;
import com.rabobank.stmtval.serviceimpl.StatementValidatorServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StatementValidatorServiceImplTest {
	private static final Logger logger = LoggerFactory.getLogger(StatementValidatorServiceImplTest.class);

	@Autowired
	private StatementValidatorService statementValidatorService;

	@Autowired
	private Environment env;

	private List<Statement> stmtList = new ArrayList<Statement>();
	
	private Set<ValidatedStatement> valStmtList = new HashSet<ValidatedStatement>();
	
	@Mock CsvFileExtractorServiceImpl csvFileExtractorServiceImpl;
	
	@InjectMocks StatementValidatorServiceImpl statementValidatorServiceImpl;

	int duplPresence = 0, mutationDiffPresence = 0;

	@Before
	public void loadData() {
		Statement stmtOne = new Statement();
		stmtOne.setReference(194261);
		stmtOne.setAccountNumber("NL91RABO0315273637");
		stmtOne.setDescription("Clothes from Jan Bakker");
		stmtOne.setStartBalance(21.6);
		stmtOne.setMutation(-41.83);
		stmtOne.setEndBalance(-20.23);
		stmtList.add(stmtOne);
		
		ValidatedStatement valStmt = new ValidatedStatement();
		valStmt.setReference(194261);
		valStmt.setDescription("Statement have duplicated reference and invalid mutation");
		valStmtList.add(valStmt);
	}

	@Test
	public void testStatementValidator() {
		String filePath = env.getProperty("document.path");
		logger.info("filePath-->" + filePath);
		Set<ValidatedStatement> valStmt = statementValidatorService.generateReportDataByValidatingStatement(filePath);
		valStmt.forEach(v -> {
			logger.info("reference->" + v.getReference() + ", " + "description->" + v.getDescription());
		});
		Set<String> st = valStmt.stream().map(v -> v.getDescription()).collect(Collectors.toSet());
		duplPresence = 0;
		mutationDiffPresence = 0;
		st.forEach(v -> {
			if (v.contains("duplicated reference")) {
				duplPresence = 1;
			} else if (v.contains("invalid mutation")) {
				mutationDiffPresence = 1;
			}
		});
		assertTrue(duplPresence > 0 || mutationDiffPresence > 0);
		assertThat(duplPresence, is(1));
		assertThat(mutationDiffPresence, is(1));
	}
	
	@Test
	public void testCsvFileExtractorUsingMockito() {
		String filePath = env.getProperty("document.path");
		logger.info("testXmlFileExtractorUsingMockito: fileName-->" + filePath);
		try {
			when(statementValidatorServiceImpl.generateReportDataByValidatingStatement(filePath)).thenReturn(valStmtList);
			assertEquals(csvFileExtractorServiceImpl.extractData(filePath).size(), 1);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
