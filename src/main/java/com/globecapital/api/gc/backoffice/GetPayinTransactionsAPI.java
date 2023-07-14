package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCAPI_v1;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetPayinTransactionsAPI extends GCAPI_v1<GetPayinTransactionsRequest, GetPayinTransactionsResponse> {

	public GetPayinTransactionsAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getPayinTransactions"));
	}

}

