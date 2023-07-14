package com.globecapital.api.spyder.quote;

import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.api.spyder.generics.SpyderRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class FOAPIRequest extends SpyderRequest{
	
	public FOAPIRequest() throws AppConfigNoKeyFoundException {
		super();
	}
	
	public void setScripCode(String sScripCode) {
		addParam(SpyderConstants.SCRIP_CODE, sScripCode);
	}
	
}
