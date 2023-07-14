package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetTradeBookAPI extends FTApi<FTRequest, GetTradeBookResponse> {

	public GetTradeBookAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getTradeBook"));
	}

}
