package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class WeeksLowEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public WeeksLowEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.weeks_low.equity"));
	}

}
