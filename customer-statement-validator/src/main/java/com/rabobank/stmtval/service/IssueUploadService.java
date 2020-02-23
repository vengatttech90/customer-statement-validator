package com.rabobank.stmtval.service;

import java.util.Map;
import java.util.Set;

public interface IssueUploadService {
	public Map<String, Object> uploadIssue(Set<String> filePath);
}
