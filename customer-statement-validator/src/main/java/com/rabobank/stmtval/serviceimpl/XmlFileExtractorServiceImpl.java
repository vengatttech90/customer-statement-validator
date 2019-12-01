package com.rabobank.stmtval.serviceimpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rabobank.stmtval.custom.exception.DocExtractionException;
import com.rabobank.stmtval.model.Statement;
import com.rabobank.stmtval.model.Statements;
import com.rabobank.stmtval.service.DocExtractorService;

@Service
public class XmlFileExtractorServiceImpl implements DocExtractorService {
	private static final Logger logger = LoggerFactory.getLogger(XmlFileExtractorServiceImpl.class);

	@Override
	public List<Statement> extractData(String file) {
		logger.info("XmlFileExtractorServiceImpl: file-->"+file);
		List<Statement> records = new ArrayList<Statement>();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Statements.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Statements lstRecord = (Statements) jaxbUnmarshaller.unmarshal(new File(file));
			records = lstRecord.getReocords();
		} catch (JAXBException e) {
			logger.error("Exception occured while parsing the XML file {} ", e.getMessage());
			logger.debug("Exception occured while parsing the XML file {} ", e);
			throw new DocExtractionException("Exception occured while parsing the XML file");
		} catch (Exception e) {
			logger.error("Exception occured while parsing the file {} ", e.getMessage());
			logger.debug("Exception occured while parsing the file {} ", e);
			throw new DocExtractionException("Exception occured while parsing the XML file");
		}
		return records;
	}
}