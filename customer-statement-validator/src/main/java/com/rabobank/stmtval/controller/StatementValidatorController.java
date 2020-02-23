package com.rabobank.stmtval.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rabobank.stmtval.constant.AppConstant;
import com.rabobank.stmtval.custom.exception.DocExtractionException;
import com.rabobank.stmtval.model.ValidatedStatement;
import com.rabobank.stmtval.service.StatementValidatorService;

@RestController
@RequestMapping("report")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class StatementValidatorController {
	private static final Logger logger = LoggerFactory.getLogger(StatementValidatorController.class);
	@Autowired
	private StatementValidatorService statementValidatorService;

	@Autowired
	private Environment env;

	// validating multple customer's statement from customer A, B , C and generating
	// Excel report
	@RequestMapping(path = "/excel/validatedStatement", method = RequestMethod.GET, produces = MediaType.ALL_VALUE, consumes = MediaType.ALL_VALUE)
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

	@RequestMapping(path = "/extract/issues", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Map<String, Object>  uploadAndGetUploadedValue(@RequestParam("files") MultipartFile[] files)
			throws DocExtractionException {
		logger.info("uploadAndGetUploadedValue:files==>" + files[0].getOriginalFilename());
		Set<String> fileLocation = new HashSet<String>();
		Map<String, Object>  issueSet = new HashMap<String, Object>();
		try {
			File fileDir = new File(env.getProperty("issues.upload.path"));
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			for (MultipartFile file : files) {
				logger.info("uploadAndGetUploadedValue:get file==>" + file.getOriginalFilename());
				File convMultipartToFile = new File(fileDir.getAbsolutePath() + File.separator +file.getOriginalFilename());
				file.transferTo(convMultipartToFile);
				logger.info("File.separator-->"+File.separator);
				logger.info("convMultipartToFile:filePath-->"+convMultipartToFile.getAbsolutePath());
				fileLocation.add(convMultipartToFile.getAbsolutePath());
			}
			issueSet = statementValidatorService.uploadFileAndFetchData(fileLocation);
		} catch (Exception e) {
			logger.error("Exception occured while generating the statement validated report in the json format is {}",
					e.getMessage());
			logger.debug("Exception occured while generating the statement validated report in the json format is {}",
					e);
			throw new DocExtractionException(
					"Exception occured while generating the statement validated report in the excel format");
		}
		return issueSet;
	}
}