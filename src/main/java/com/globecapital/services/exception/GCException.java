package com.globecapital.services.exception;

public class GCException extends Exception {
	private static final long serialVersionUID = 1L;
	private String infoId;

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public GCException(String infoID, String message, Throwable cause) {
		super(message, cause);
		this.setInfoId(infoID);
	}

	public GCException(String infoID, String message) {
		super(message);
		this.setInfoId(infoID);
	}

	public GCException(String infoID) {
		this.setInfoId(infoID);
	}
}
