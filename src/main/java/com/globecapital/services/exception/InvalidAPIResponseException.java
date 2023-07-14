package com.globecapital.services.exception;

import com.globecapital.constants.InfoIDConstants;

public class InvalidAPIResponseException extends GCException {

	private static final long serialVersionUID = 1L;

	public InvalidAPIResponseException(String message, Throwable cause) {
		super(InfoIDConstants.DYNAMIC_MSG, message, cause);
	}

	public InvalidAPIResponseException(String message) {
		super(InfoIDConstants.DYNAMIC_MSG, message);
	}

}
