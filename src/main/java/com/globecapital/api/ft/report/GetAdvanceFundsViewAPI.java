package com.globecapital.api.ft.report;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class GetAdvanceFundsViewAPI extends 
				FTApi<GetAdvanceFundsViewRequest, GetAdvanceFundsViewResponse> {
	
	public GetAdvanceFundsViewAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getAdvanceFundsView"));
	}

}