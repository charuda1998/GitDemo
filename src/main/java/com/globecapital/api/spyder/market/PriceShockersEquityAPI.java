package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class PriceShockersEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public PriceShockersEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.price_shockers.equity"));
	}

}
