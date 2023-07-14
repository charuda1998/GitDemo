package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class GetNetPositionAPI extends FTApi<FTRequest, GetNetPositionResponse> {
	
	public GetNetPositionAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getNetPosition"));
	}
	
}
