package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse extends FTResponse {
	
	@SerializedName("ResponseObject")
	protected ForgotPasswordObject responseObject;

	public ForgotPasswordObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(ForgotPasswordObject responseObject) {
		this.responseObject = responseObject;
	}

}
