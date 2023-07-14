package com.globecapital.api.spyder.quote;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class CurrencyAPI extends SpyderApi<CurrencyAPIRequest, CurrencyAPIResponse>{

	public CurrencyAPI() throws GCException {
		super(AppConfig.getValue("spyder.api.fo_currency"));
	}

}
