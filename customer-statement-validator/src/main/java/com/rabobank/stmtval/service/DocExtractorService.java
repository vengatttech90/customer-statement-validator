package com.rabobank.stmtval.service;

import java.util.List;

import com.rabobank.stmtval.model.Statement;

public interface DocExtractorService {
	public List<Statement> extractData(String file);
}
