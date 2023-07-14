package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class TopGainersCommodityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public TopGainersCommodityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.top_gainers.commodity"));
	}

}
