package com.rabobank.stmtval.serviceimpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rabobank.stmtval.constant.AppConstant;
import com.rabobank.stmtval.custom.exception.DocExtractionException;
import com.rabobank.stmtval.factory.DocExtractorServiceFactory;
import com.rabobank.stmtval.model.Statement;
import com.rabobank.stmtval.model.ValidatedStatement;
import com.rabobank.stmtval.service.DocExtractorService;
import com.rabobank.stmtval.service.StatementValidatorService;
import com.rabobank.stmtval.util.ReportValidatorUtil;

@Service
public class StatementValidatorServiceImpl implements StatementValidatorService {
	private static final Logger logger = LoggerFactory.getLogger(StatementValidatorServiceImpl.class);

	@Autowired
	private DocExtractorServiceFactory docExtractorServiceFactory;

	@Autowired
	private ReportValidatorUtil reportGeneratorUtil;

	private int rowNum = 1;
	public Set<ValidatedStatement> generateReportDataByValidatingStatement(String filePath) {
		List<Statement> multipleStmt = new ArrayList<Statement>();
		Set<ValidatedStatement> finalReportData = new HashSet<ValidatedStatement>();
		try (Stream<Path> navigateToPath = Files.walk(Paths.get(filePath));) {
			List<String> filesList = navigateToPath.filter(Files::isRegularFile).map(x -> x.toString())
					.collect(Collectors.toList());
			filesList.forEach(fileName -> {
				String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
				try {
					DocExtractorService docExtractorService = docExtractorServiceFactory.getDocExtractor(fileExt);
					multipleStmt.addAll(docExtractorService.extractData(fileName));
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			});
			finalReportData = reportGeneratorUtil.validatedStatements(reportGeneratorUtil.identifyDuplRef(multipleStmt),
					reportGeneratorUtil.identifyInvalidMutations(multipleStmt), multipleStmt);
		} catch (IOException | DocExtractionException e) {
			logger.error("Exception occured while reading and validating data from the document is {}", e.getMessage());
			logger.debug(
					"Trace logs for an exception occured while reading and validating data from the document is {}", e);
		}
		return finalReportData;
	}

	public ByteArrayInputStream generateFinalizedReport(Set<ValidatedStatement> finalData) {
		try (SXSSFWorkbook workBook = new SXSSFWorkbook(50);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			Sheet sheet = workBook.createSheet("Validated_Statement");
			Font headerFont = workBook.createFont();
			headerFont.setBold(true);
			headerFont.setColor(IndexedColors.BLACK.getIndex());

			CellStyle cellStyle = workBook.createCellStyle();
			cellStyle.setFont(headerFont);

			Row headerRow = sheet.createRow(0);
			for (int col = 0; col < AppConstant.columns.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(AppConstant.columns[col]);
				cell.setCellStyle(cellStyle);
			}
			finalData.forEach(vs -> {
				Row row = sheet.createRow(rowNum++);
				int cellNum = 0;
				row.createCell(cellNum++).setCellValue(vs.getReference());
				row.createCell(cellNum++).setCellValue(vs.getDescription());
			});
			workBook.write(outputStream);
			ByteArrayInputStream streamData = new ByteArrayInputStream(outputStream.toByteArray());
			outputStream.flush();
			return streamData;
		} catch (IOException | DocExtractionException e) {
			logger.error("Exception occured while generating the statement validated report is {}", e.getMessage());
			logger.debug("Trace logs for an exception occured while generating the statement validated report is {}",
					e);
		}
		return null;
	}
}