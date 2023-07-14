package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class LogOffAPI extends FTApi<LogOffRequest, LogOffResponse> {

	public LogOffAPI() throws GCException {
		super(AppConfig.getValue("moon.api.logoff"));
	}

}
