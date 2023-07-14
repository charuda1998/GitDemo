package com.globecapital.services.exception;

import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;

public class InvalidSession extends GCException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidSession() {
		super(InfoIDConstants.INVALID_SESSION, InfoMessage.getInfoMSG("info_msg.invalid.invalid_session"));
	}
	
	public InvalidSession(String msg) {
		super(InfoIDConstants.INVALID_SESSION, msg);
	}
}
