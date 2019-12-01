package com.rabobank.stmtval.serviceimpl;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rabobank.stmtval.custom.exception.DocExtractionException;
import com.rabobank.stmtval.model.Statement;
import com.rabobank.stmtval.service.DocExtractorService;

@Service
public class CsvFileExtractorServiceImpl implements DocExtractorService {
	private static final Logger logger = LoggerFactory.getLogger(CsvFileExtractorServiceImpl.class);

	public List<Statement> extractData(String file) {
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
		List<Statement> stmtList = new ArrayList<Statement>();
		try (CSVParser parser = new CSVParser(new FileReader(file), format);) {
			parser.forEach((data) -> {
				Statement stmt = new Statement();
				stmt.setReference(Long.parseLong(data.get("Reference")));
				stmt.setAccountNumber(data.get("AccountNumber"));
				stmt.setDescription(data.get("Description"));
				stmt.setStartBalance(Double.parseDouble(data.get("Start Balance")));
				stmt.setMutation(Double.parseDouble(data.get("Mutation")));
				stmt.setEndBalance(Double.parseDouble(data.get("End Balance")));
				stmtList.add(stmt);
			});
		} catch (FileNotFoundException e) {
			logger.error("Exception occured while checking the presence of file {} is {}", file, e.getMessage());
			logger.debug("Trace logs for an exception occured while checking the presence of file {} is {}", file, e);
			throw new DocExtractionException("Exception occured while checking the presence of file" + file);
		} catch (IOException e) {
			logger.error("Exception occured while reading the file {} is {}", file, e.getMessage());
			logger.debug("Trace logs for an exception occured while reading the file {} is {}", file, e);
			throw new DocExtractionException("Exception occured while reading the file" + file);
		} catch (Exception e) {
			logger.info("Exception occured while processing the file {} is {}", file, e.getMessage());
			logger.debug("Trace logs for an exception occured while processing the file {} is {}", file, e);
			throw new DocExtractionException("Exception occured while processing the file" + file);
		}
		return stmtList;
	}
}