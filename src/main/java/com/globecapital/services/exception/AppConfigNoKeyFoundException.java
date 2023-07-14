package com.globecapital.services.exception;

import com.globecapital.constants.InfoIDConstants;

public class AppConfigNoKeyFoundException extends GCException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AppConfigNoKeyFoundException(String msg) {
		super(InfoIDConstants.DYNAMIC_MSG, msg);
	}
}
