package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;
public class GenerateOTPAPI extends FTApi<GenerateOTPRequest, GenerateOTPResponse> {

	public GenerateOTPAPI() throws GCException {
		super(AppConfig.getValue("moon.api.generate.new.otp"));

	}
}
