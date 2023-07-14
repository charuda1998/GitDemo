package com.globecapital.services.exception;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;

public class InvalidRequestKeyException extends GCException {	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidRequestKeyException() {
		super(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.invalid.invalid_request_parameter"));
	}

	public InvalidRequestKeyException(String msg) {
		super(InfoIDConstants.DYNAMIC_MSG, msg);
	}
	

}
