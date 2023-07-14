package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class OIGainersCommodityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public OIGainersCommodityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.oi_gainers.commodity"));
	}

}
