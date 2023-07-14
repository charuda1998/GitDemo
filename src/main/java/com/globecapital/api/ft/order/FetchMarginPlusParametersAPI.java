package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class FetchMarginPlusParametersAPI extends FTApi<FetchMarginPlusParamsRequest, FetchMarginPlusParamsResponse>{
	public FetchMarginPlusParametersAPI() throws GCException {
		super(AppConfig.getValue("moon.api.fetchMarginPlusParams"));

	}
}
