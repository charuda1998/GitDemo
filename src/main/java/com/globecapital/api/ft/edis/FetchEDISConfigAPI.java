package com.globecapital.api.ft.edis;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class FetchEDISConfigAPI extends FTApi<GetEDISConfigRequest, GetEDISConfigResponse> {

	public FetchEDISConfigAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getEdisConfigDetails"));

	}

}
