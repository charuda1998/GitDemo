package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class SetAuthenticationStatusAPI extends FTApi<SetAuthenticationStatusRequest, SetAuthenticationStatusResponse> {
	
	public SetAuthenticationStatusAPI() throws GCException {
		super(AppConfig.getValue("moon.api.authenticationStatus"));

	}
	

}
