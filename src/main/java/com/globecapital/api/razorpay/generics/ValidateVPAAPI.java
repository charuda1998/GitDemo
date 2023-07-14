package com.globecapital.api.razorpay.generics;

import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class ValidateVPAAPI extends RazorPayAPI<ValidateVPARequest, ValidateVPAResponse> {

	public ValidateVPAAPI() throws GCException {
		super(AppConfig.getValue("razorpay.api.validateVPA"));
	}

}

