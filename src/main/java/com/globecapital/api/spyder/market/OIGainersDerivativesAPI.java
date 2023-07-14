package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class OIGainersDerivativesAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public OIGainersDerivativesAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.oi_gainers.derivatives"));
	}

}
