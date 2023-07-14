package com.globecapital.api.ft.market;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class GetExchangeMessagesAPI extends FTApi<GetExchangeMessagesRequest, GetExchangeMessagesResponse> {

	public GetExchangeMessagesAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getExchangeMessages"));

	}

}
