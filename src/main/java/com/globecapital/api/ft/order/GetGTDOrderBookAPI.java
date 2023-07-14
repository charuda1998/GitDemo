package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class GetGTDOrderBookAPI extends FTApi<GetGTDOrderBookRequest, GetGTDOrderBookResponse> {
	
	public GetGTDOrderBookAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getGTDOrderBook"));

	}
	

}

