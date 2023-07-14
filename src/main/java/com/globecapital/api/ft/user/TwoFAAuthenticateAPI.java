package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class TwoFAAuthenticateAPI extends FTApi<TwoFAAuthenticationRequest, TwoFAAuthenticateResponse> {
	
	public TwoFAAuthenticateAPI() throws GCException {
		super(AppConfig.getValue("moon.api.twoFAAuthenticationApi"));
	}

}
