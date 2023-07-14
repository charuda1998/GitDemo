package com.globecapital.services.exception;
import com.globecapital.constants.InfoIDConstants;

public class DynamicException extends GCException {

    private static final long serialVersionUID = 1L;

    public DynamicException(String errorMsg) {
		super(InfoIDConstants.DYNAMIC_MSG, errorMsg);
	}
    
}