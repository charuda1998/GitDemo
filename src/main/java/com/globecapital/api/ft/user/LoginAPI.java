package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;
public class LoginAPI extends FTApi<LoginRequest, LoginResponse> {

	public LoginAPI() throws GCException {
		super(AppConfig.getValue("moon.api.login"));

	}
}
