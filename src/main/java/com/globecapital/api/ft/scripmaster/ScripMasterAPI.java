package com.globecapital.api.ft.scripmaster;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class ScripMasterAPI extends FTApi<FTRequest, ScripMasterResponse> {

	public ScripMasterAPI() throws GCException {
		super(AppConfig.getValue("moon.api.scripMaster"));

	}

}
