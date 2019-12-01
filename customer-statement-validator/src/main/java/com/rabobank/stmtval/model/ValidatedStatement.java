package com.rabobank.stmtval.model;

import java.io.Serializable;

public class ValidatedStatement implements Serializable {
	private static final long serialVersionUID = 3620806601937111956L;
	private long reference;
	private String description;

	public long getReference() {
		return reference;
	}

	public void setReference(long reference) {
		this.reference = reference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + this.getReference());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		if (this.reference != ((ValidatedStatement) obj).reference)
			return false;
		if (this == obj)
			return true;
		return true;
	}
}
