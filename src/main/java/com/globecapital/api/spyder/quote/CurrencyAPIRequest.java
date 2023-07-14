package com.globecapital.api.spyder.quote;

import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.api.spyder.generics.SpyderRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class CurrencyAPIRequest extends SpyderRequest{
	public CurrencyAPIRequest() throws AppConfigNoKeyFoundException {
		super();
	}
	
	public void setExch(String sExch) {
		addParam(SpyderConstants.EXCH, sExch);
	}
	
	public void setScripCode(String sScripCode) {
		addParam(SpyderConstants.SCRIP_CODE, sScripCode);
	}

}
