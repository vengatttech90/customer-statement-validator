package com.rabobank.stmtval.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.decimal4j.util.DoubleRounder;
import org.springframework.stereotype.Component;

import com.rabobank.stmtval.model.Statement;
import com.rabobank.stmtval.model.ValidatedStatement;

@Component
public class ReportValidatorUtil {
//	private static final Logger logger = LoggerFactory.getLogger(StatementValidatorServiceImpl.class);

	public Set<Statement> identifyDuplRef(List<Statement> stmt) {
		Set<Statement> duplStmt = stmt.stream().collect(Collectors.groupingBy(Statement::getReference)).entrySet()
				.stream().filter(data -> {
					return data.getValue().size() > 1;
				}).flatMap(e -> {
					return e.getValue().stream();
				}).collect(Collectors.toSet());
		return duplStmt;
	}

	public Set<Statement> identifyInvalidMutations(List<Statement> stmt) {
		Set<Statement> invMut = new HashSet<Statement>();
		invMut = stmt.stream().filter(v -> {
			double sum = v.getStartBalance() + v.getMutation();
			sum = DoubleRounder.round(sum, 2);
			return v.getEndBalance() != sum;
		}).collect(Collectors.toSet());
		return invMut;
	}

	public Set<ValidatedStatement> validatedStatements(Set<Statement> duplRef, Set<Statement> invMut,
			List<Statement> stmtList) {
		Set<ValidatedStatement> valStmtList = new HashSet<ValidatedStatement>();
		stmtList.forEach(stmt -> {
			ValidatedStatement valStmt = new ValidatedStatement();
			valStmt.setReference(stmt.getReference());
			StringBuilder desc = new StringBuilder();
			if (duplRef.contains(stmt)) {
				desc.append("Found duplicated reference");
			}
			if (invMut.contains(stmt)) {
				if (desc.length() > 0) {
					desc.append(" and invalid mutation");
				} else {
					desc.append("Found invalid mutation");
				}
			}
			if (desc.length() == 0) {
				desc.append("No issue with the statement");
			}
			valStmt.setDescription(desc.toString());
			valStmtList.add(valStmt);
		});
		return valStmtList;
	}
}
