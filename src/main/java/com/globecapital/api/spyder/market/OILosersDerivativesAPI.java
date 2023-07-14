package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class OILosersDerivativesAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public OILosersDerivativesAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.oi_losers.derivatives"));
	}

}
