package com.globecapital.api.spyder.market;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class UpperCircuitEquityAPI extends SpyderApi<MarketMoversRequest, MarketMoversResponse> {

	public UpperCircuitEquityAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.upper_circuit.equity"));
	}

}
