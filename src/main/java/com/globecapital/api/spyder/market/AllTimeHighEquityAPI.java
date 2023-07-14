package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class AllTimeHighEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public AllTimeHighEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.all_time_high.equity"));
	}

}
