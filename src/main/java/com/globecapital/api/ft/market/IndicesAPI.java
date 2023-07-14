package com.globecapital.api.ft.market;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class IndicesAPI extends FTApi<FTRequest, IndicesResponse> {

	public IndicesAPI() throws GCException {
		super(AppConfig.getValue("moon.api.Index"));

	}

}
