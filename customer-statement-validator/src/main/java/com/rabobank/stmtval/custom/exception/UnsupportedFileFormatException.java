package com.rabobank.stmtval.custom.exception;

public class UnsupportedFileFormatException extends RuntimeException {
	private static final long serialVersionUID = 635531764722790700L;

	public UnsupportedFileFormatException(String message) {
		super(message);
	}
}
