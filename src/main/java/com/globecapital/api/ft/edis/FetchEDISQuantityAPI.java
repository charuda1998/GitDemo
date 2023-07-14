package com.globecapital.api.ft.edis;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class FetchEDISQuantityAPI extends FTApi<GetEDISQuantityRequest, GetEDISQuantityResponse> {

	public FetchEDISQuantityAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getEdisQuantityDetails"));

	}

}
