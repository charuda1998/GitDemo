package com.globecapital.api.spyder.quote;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class FOAPI extends SpyderApi<FOAPIRequest, FOAPIResponse>{

	public FOAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.fo_api"));
	}

}
