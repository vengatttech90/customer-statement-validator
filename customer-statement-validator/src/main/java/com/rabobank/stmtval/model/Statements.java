package com.rabobank.stmtval.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "records")
@XmlAccessorType(XmlAccessType.FIELD)
public class Statements {
	
	@XmlElement(name = "record")
	List<Statement> reocords = new ArrayList<Statement>();

	public List<Statement> getReocords() {
		return reocords;
	}

	public void setReocords(List<Statement> reocords) {
		this.reocords = reocords;
	}
}
