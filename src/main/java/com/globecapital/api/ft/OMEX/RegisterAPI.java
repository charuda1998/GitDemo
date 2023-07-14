package com.globecapital.api.ft.OMEX;

import com.globecapital.api.ft.OMEX.generics.OmexApi;
import com.globecapital.api.ft.OMEX.generics.OmexRequest;
import com.globecapital.api.ft.OMEX.generics.OmexResponse;
import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class RegisterAPI extends  OmexApi<OmexRequest, OmexResponse> {
	
	public RegisterAPI() throws GCException {
		super(AppConfig.getValue("omex.api.registerAPIRequest"));

	}
}
