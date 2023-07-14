package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class TopGainersEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public TopGainersEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.top_gainers.equity"));
	}

}
