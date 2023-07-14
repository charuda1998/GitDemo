package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetDerivativeTransactionAPI extends GCApi<GetTransactionRequest, GetTransactionResponse> {

	public GetDerivativeTransactionAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getDerivativeTransaction"));
	}
}