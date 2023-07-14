package com.globecapital.api.spyder.quote;

import com.globecapital.api.spyder.generics.SpyderApi;
import com.globecapital.services.exception.GCException;
import com.msf.cmots.config.AppConfig;


public class FORolloverAPI extends SpyderApi<FORolloverRequest, FORolloverResponse>{

	public FORolloverAPI() throws GCException{
		super(AppConfig.getValue("spyder.api.fo_rollover"));
	}

}
