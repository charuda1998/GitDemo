package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class MostActiveByValueEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public MostActiveByValueEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.most_active_by_value.equity"));
	}

}
