package com.rabobank.test.stmtval.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import com.rabobank.stmtval.constant.DocExtentionEnum;
import com.rabobank.stmtval.factory.DocExtractorServiceFactory;
import com.rabobank.stmtval.model.Statement;
import com.rabobank.stmtval.service.DocExtractorService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CsvFileExtractorServiceImplTest {
	private static final Logger logger = LoggerFactory.getLogger(CsvFileExtractorServiceImplTest.class);
	@Autowired
	private DocExtractorServiceFactory docExtractorServiceFactory;

	@Autowired
	private Environment env;

	private List<Statement> stmtList = new ArrayList<Statement>();

	@Before
	public void loadData() {
		Statement stmtOne = new Statement();
		stmtOne.setReference(194261);
		stmtOne.setAccountNumber("NL91RABO0315273637");
		stmtOne.setDescription("Clothes from Jan Bakker");
		stmtOne.setStartBalance(21.6);
		stmtOne.setMutation(-41.83);
		stmtOne.setEndBalance(-20.23);
		Statement stmtTwo = new Statement();
		stmtOne.setReference(130498);
		stmtOne.setAccountNumber("NL69ABNA0433647324");
		stmtOne.setDescription("Tickets for Peter Theuß");
		stmtOne.setStartBalance(26.9);
		stmtOne.setMutation(26.9);
		stmtOne.setEndBalance(8.12);
		stmtList.add(stmtOne);
		stmtList.add(stmtTwo);
	}

	@Test
	public void testCsvFileExtractor() {
		String fileName = env.getProperty("document.name");
		logger.info("fileName-->" + fileName);
		String fileExtn = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		if (StringUtils.equalsIgnoreCase(fileExtn.toLowerCase(), DocExtentionEnum.csv.toString())) {
			try {
				DocExtractorService docExtract = docExtractorServiceFactory.getDocExtractor(fileExtn);
				List<Statement> stmtReport = docExtract.extractData(fileName);
				stmtReport.forEach(v -> {
					logger.info("--->" + v.toString());
				});
				logger.info("check two collections having any matching elements -->");
				assertEquals(CollectionUtils.containsAny(
						stmtReport.stream().map(v -> v.getReference()).collect(Collectors.toSet()),
						stmtList.stream().map(v -> v.getReference()).collect(Collectors.toSet())), true);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}else {
			logger.info("This class is specifically created to test csv file extraction. please change the file name in the properties file -->");
			assertEquals(DocExtentionEnum.csv.toString(), fileExtn.toLowerCase());
		}

	}
}
