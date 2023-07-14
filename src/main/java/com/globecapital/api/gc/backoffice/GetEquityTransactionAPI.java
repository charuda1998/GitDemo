package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetEquityTransactionAPI extends GCApi<GetTransactionRequest, GetEquityTransactionResponse> {

	public GetEquityTransactionAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getEquityTransaction"));
	}
}