package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.api.spyder.generics.SpyderRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class OiAnalysisRequest extends SpyderRequest {
	
	public OiAnalysisRequest() throws AppConfigNoKeyFoundException {
		super();
	}

	public void setBuild(String build) {
		addParam(SpyderConstants.BUILD, build);
	}
}
