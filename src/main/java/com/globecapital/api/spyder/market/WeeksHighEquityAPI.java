package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class WeeksHighEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public WeeksHighEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.weeks_high.equity"));
	}

}
