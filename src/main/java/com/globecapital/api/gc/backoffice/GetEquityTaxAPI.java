package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetEquityTaxAPI extends GCApi<GetTaxRequest, GetTaxResponse> {

	public GetEquityTaxAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getEquityTaxReport"));
	}
}