package com.globecapital.api.ft.user;

import java.util.List;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class TwoFAAuthenticateResponse extends FTResponse {
	@SerializedName("ResponseObject")
	protected List<TwoFAAuthResponseObject> responseObject;

	public List<TwoFAAuthResponseObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<TwoFAAuthResponseObject> responseObject) {
		this.responseObject = responseObject;
	}

}
