package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class MostActiveByVolumeEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public MostActiveByVolumeEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.most_active_by_volume.equity"));
	}

}
