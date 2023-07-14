package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.api.spyder.generics.SpyderRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class MarketMoversRequest  extends SpyderRequest {

	public MarketMoversRequest() throws AppConfigNoKeyFoundException {
		super();
	}
	
	public void setExch(String sExch) {
		addParam(SpyderConstants.EXCH, sExch);
	}
	
	public void setType(String sType) {
		addParam(SpyderConstants.TYPE, sType);
	}
}
