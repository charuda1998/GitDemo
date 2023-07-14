package com.globecapital.services.exception;

import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;

public class RequestFailedException extends GCException {
	private static final long serialVersionUID = 1L;
	
	public RequestFailedException() {
		super(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.invalid.request_failed"));
	}
	
	public RequestFailedException(String message) {
		super(InfoIDConstants.DYNAMIC_MSG, message);
	}
	
}
