package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class VolumeShockersEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public VolumeShockersEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.volume_shockers.equity"));
	}

}
