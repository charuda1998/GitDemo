package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetMarketStatusResponse extends FTResponse {
	
	@SerializedName("ResponseObject")
	protected GetMarketStatusResponseObject responseObject;

	public GetMarketStatusResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(GetMarketStatusResponseObject responseObject) {
		this.responseObject = responseObject;
	}
	
}
