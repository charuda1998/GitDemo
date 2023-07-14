package com.globecapital.api.razorpay.generics;

import org.json.JSONException;

import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;

public class ValidateVPARequest extends RazorPayRequest {

	public ValidateVPARequest() throws AppConfigNoKeyFoundException {
		super();
	}
	
	public void setVPA(String vpa) throws JSONException, GCException {
		addToReq(RazorPayConstants.VPA, vpa);
	}

	@Override
	public String toString() {
		return this.reqObj.toString();
	}
}
