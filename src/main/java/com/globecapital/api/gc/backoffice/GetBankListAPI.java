package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class GetBankListAPI extends GCApi<GetBankListRequest,GetBankListResponse> {

	public GetBankListAPI() throws GCException {
		super(AppConfig.getValue("gc.api.getBankList"));
	}
}