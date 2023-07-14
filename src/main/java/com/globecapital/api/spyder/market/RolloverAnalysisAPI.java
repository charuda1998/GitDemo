package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class RolloverAnalysisAPI extends SpyderApi<RolloverAnalysisRequest, RolloverAnalysisResponse> {

	public RolloverAnalysisAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.rollover_analysis"));
	}


}
