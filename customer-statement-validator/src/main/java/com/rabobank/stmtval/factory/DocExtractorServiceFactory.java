package com.rabobank.stmtval.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rabobank.stmtval.constant.DocExtentionEnum;
import com.rabobank.stmtval.custom.exception.UnsupportedFileFormatException;
import com.rabobank.stmtval.service.DocExtractorService;
import com.rabobank.stmtval.serviceimpl.CsvFileExtractorServiceImpl;
import com.rabobank.stmtval.serviceimpl.XmlFileExtractorServiceImpl;

@Component
public class DocExtractorServiceFactory {

	@Autowired
	private CsvFileExtractorServiceImpl csvFileExtractorServiceImpl;

	@Autowired
	private XmlFileExtractorServiceImpl xmlFileExtractorServiceImpl;

	public DocExtractorService getDocExtractor(String docExt) {
		if (docExt.equals(DocExtentionEnum.csv.toString())) {
			return csvFileExtractorServiceImpl;
		} else if (docExt.equals(DocExtentionEnum.xml.toString())) {
			return xmlFileExtractorServiceImpl;
		} else {
			throw new UnsupportedFileFormatException("The documentf format " + docExt + " is not supported");
		}
	}
}