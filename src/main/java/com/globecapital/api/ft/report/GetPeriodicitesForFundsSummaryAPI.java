package com.globecapital.api.ft.report;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class GetPeriodicitesForFundsSummaryAPI extends 
				FTApi<GetPeriodicitesForFundsSummaryRequest, GetPeriodicitesForFundsSummaryResponse> {
	
	public GetPeriodicitesForFundsSummaryAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getPeriodicitesForFundsSummary"));
	}

}