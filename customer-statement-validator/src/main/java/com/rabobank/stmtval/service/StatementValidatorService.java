package com.rabobank.stmtval.service;

import java.io.ByteArrayInputStream;
import java.util.Set;

import com.rabobank.stmtval.model.ValidatedStatement;

public interface StatementValidatorService {
	public Set<ValidatedStatement> generateReportDataByValidatingStatement(String filePath);

	public ByteArrayInputStream generateFinalizedReport(Set<ValidatedStatement> finalData);
}
