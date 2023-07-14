package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.api.spyder.generics.SpyderRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class RolloverAnalysisRequest extends SpyderRequest {
	
	public RolloverAnalysisRequest() throws AppConfigNoKeyFoundException {
		super();
	}

	public void setStock(String scripType) {
		addParam(SpyderConstants.STOCK, scripType);
	}
	
	public void setScripType(String scripType) {
		addParam(SpyderConstants.SCRIP_TYPE, scripType);
	}
	
	public void setRolloverType(String rolloverType) {
		addParam(SpyderConstants.ROLLOVER_TYPE, rolloverType);
	}

}
