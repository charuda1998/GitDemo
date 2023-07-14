package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class TopLosersCommodityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public TopLosersCommodityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.top_losers.commodity"));
	}

}
