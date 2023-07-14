package com.globecapital.api.razorpay.generics;

import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.api.generics.ApiRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class RazorPayRequest extends ApiRequest {

	public RazorPayRequest() throws AppConfigNoKeyFoundException {
		this.respType = RESP_TYPE.JSON;
		this.apiVendorName=RazorPayConstants.VENDOR_NAME;
	}
}
