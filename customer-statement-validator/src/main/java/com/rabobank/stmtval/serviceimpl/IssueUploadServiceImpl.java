package com.rabobank.stmtval.serviceimpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.rabobank.stmtval.custom.exception.DocExtractionException;
import com.rabobank.stmtval.service.IssueUploadService;

@Service
public class IssueUploadServiceImpl implements IssueUploadService {
	private static final Logger logger = LoggerFactory.getLogger(StatementValidatorServiceImpl.class);

	@Override
	public Map<String, Object> uploadIssue(Set<String> fileSet) {
		Map<String, Object> issueMap = extractFileWithDynamicHeader(fileSet);
		return issueMap;
	}

	public Map<String, Object> extractFileWithDynamicHeader(Set<String> fileSet) {
		System.out.println("extractFile:fileSet==>" + fileSet.toString());
		Map<String, String> columns = new LinkedHashMap<String, String>();
		List<String> colList = new ArrayList<String>();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> nestedMap = new LinkedHashMap<String, Object>();
		for (String file : fileSet) {
			System.out.println("file==>" + file);
			try (BufferedReader fileReader = new BufferedReader(new FileReader(file));) {
				String line = "";
				int cnt = 0;
				while ((line = fileReader.readLine()) != null) {
//					System.out.println("line=>"+line);
					String[] tokens = line.split(",");
					System.out.println("token==>" + tokens.toString());
					if (cnt == 0) {
						for (String token : tokens) {
							System.out.println("token==>" + token);
							colList.add(token);
						}
					} else {
						Map<String, Object> data = new LinkedHashMap<String, Object>();
						int k = 0;
						for (String token : tokens) {
							System.out.println("colList.get(k)==>" + colList.get(k));
							String key = colList.get(k).replaceAll("\\s+", "").toLowerCase();
							data.put(key, token);
							k = k + 1;
						}
						dataList.add(data);
					}
					cnt = cnt + 1;
				}
				colList.forEach(v -> {
					String key = v.replaceAll("\\s+", "").toLowerCase();
					System.out.println("key==>" + key);
					columns.put(key, v);
				});
				nestedMap.put("columns", columns);
				nestedMap.put("data", dataList);
			} catch (FileNotFoundException e) {
				logger.error("Exception occured while importing the data from file {} is {}", file, e.getMessage());
				logger.debug("Trace logs for an exception occured while importing the data from file {} is {}", file,
						e);
				throw new DocExtractionException("Exception occured while checking the presence of file" + file);
			} catch (IOException e) {
				logger.error("Exception occured while reading the file {} is {}", file, e.getMessage());
				logger.debug("Trace logs for an exception occured while reading the file {} is {}", file, e);
				throw new DocExtractionException("Exception occured while reading the file" + file);
			} catch (Exception e) {
				System.out.println("Exception occured while processing the file {} is {}" + file + e.getMessage());
				logger.debug("Trace logs for an exception occured while processing the file {} is {}", file, e);
				throw new DocExtractionException("Exception occured while processing the file" + file);
			}
		}
		return nestedMap;
	}
}