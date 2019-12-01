package com.rabobank.stmtval.controller;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rabobank.stmtval.constant.AppConstant;
import com.rabobank.stmtval.custom.exception.DocExtractionException;
import com.rabobank.stmtval.model.ValidatedStatement;
import com.rabobank.stmtval.service.StatementValidatorService;

@RestController
@RequestMapping("report")
public class StatementValidatorController {
	private static final Logger logger = LoggerFactory.getLogger(StatementValidatorController.class);
	@Autowired
	private StatementValidatorService statementValidatorService;

	// validating multple customer's statement from customer A, B , C and generating
	// Excel report
	@RequestMapping(path = "/excel/validatedStatement", method = RequestMethod.POST, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
	public ResponseEntity<InputStreamResource> generateExcelReportByValidatingStatement(
			@RequestParam("filePath") String filePath) {
		logger.info("StatementValidatorController: filePath-->" + filePath);
		HttpHeaders headers = new HttpHeaders();
		try {
			Set<ValidatedStatement> finalStatement = statementValidatorService
					.generateReportDataByValidatingStatement(filePath);
			ByteArrayInputStream inStream = statementValidatorService.generateFinalizedReport(finalStatement);
			headers.add("Content-Disposition", "attachment;filename=" + AppConstant.reportName);
			return ResponseEntity.ok().headers(headers).body(new InputStreamResource(inStream));
		} catch (Exception e) {
			logger.error("Exception occured while generating the statement validated report in the excel format is {}",
					e.getMessage());
			logger.debug("Exception occured while generating the statement validated report in the excel format is {}",
					e);
			throw new DocExtractionException(
					"Exception occured while generating the statement validated report in the excel format");
		}
	}

	// JSON report
	@RequestMapping(path = "/json/validatedStatement", method = RequestMethod.POST, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
	public Set<ValidatedStatement> generateJsonReportByValidatingStatement(@RequestParam("filePath") String filePath)
			throws DocExtractionException {
		Set<ValidatedStatement> finalStatement = new HashSet<ValidatedStatement>();
		try {
			finalStatement = statementValidatorService.generateReportDataByValidatingStatement(filePath);
		} catch (Exception e) {
			logger.error("Exception occured while generating the statement validated report in the json format is {}",
					e.getMessage());
			logger.debug("Exception occured while generating the statement validated report in the json format is {}",
					e);
			throw new DocExtractionException(
					"Exception occured while generating the statement validated report in the excel format");
		}
		return finalStatement;
	}
}