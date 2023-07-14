package com.globecapital.api.razorpay.generics;

import com.google.gson.annotations.SerializedName;

public class ValidateVPAResponse extends RazorPayResponse {

	@SerializedName("error")
    protected ErrorResponseObject error;
	
	@SerializedName("success")
    protected String success;

	public ErrorResponseObject getError() {
		return error;
	}

	public String getSuccess() {
		return success;
	}
}
