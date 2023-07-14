package com.globecapital.api.spyder.chart;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class HistoricalDataAPI extends SpyderApi<HistoricalDataRequest, HistoricalDataResponse>{

	public HistoricalDataAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.historical_data"));
	}

}
