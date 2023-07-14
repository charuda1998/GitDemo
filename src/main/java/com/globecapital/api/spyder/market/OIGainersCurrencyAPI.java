package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class OIGainersCurrencyAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public OIGainersCurrencyAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.oi_gainers.currency"));
	}

}
