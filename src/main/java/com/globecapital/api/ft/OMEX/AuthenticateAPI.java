package com.globecapital.api.ft.OMEX;

import com.globecapital.api.ft.OMEX.generics.OmexApi;
import com.globecapital.api.ft.OMEX.generics.OmexRequest;
import com.globecapital.api.ft.OMEX.generics.OmexResponse;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class AuthenticateAPI extends OmexApi<OmexRequest, OmexResponse> {
	
	public AuthenticateAPI() throws GCException {
		super(AppConfig.getValue("omex.api.authenticate"));

	}
	
	

}
