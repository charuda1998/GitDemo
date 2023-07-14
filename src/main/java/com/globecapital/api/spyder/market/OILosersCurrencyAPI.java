package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class OILosersCurrencyAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public OILosersCurrencyAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.oi_losers.currency"));
	}

}
