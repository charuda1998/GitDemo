package com.globecapital.api.spyder.quote;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class MCXAPI extends SpyderApi<MCXAPIRequest, MCXAPIResponse>{

	public MCXAPI() throws GCException {
		super(AppConfig.getValue("spyder.api.fo_mcx"));
	}

}
