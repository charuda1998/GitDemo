package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class LowerCircuitEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public LowerCircuitEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.lower_circuit.equity"));
	}

}
