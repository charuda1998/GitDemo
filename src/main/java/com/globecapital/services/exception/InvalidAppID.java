package com.globecapital.services.exception;

import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;

public class InvalidAppID extends GCException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidAppID() {
		super(InfoIDConstants.INVALID_APPID, InfoMessage.getInfoMSG("info_msg.invalid.app_id"));
	}
}
