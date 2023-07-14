package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class OiAnalysisDerivativesAPI extends SpyderApi<OiAnalysisRequest, OiAnalysisResponse> {

	public OiAnalysisDerivativesAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.oi_analysis_derivatives"));
	}

}
