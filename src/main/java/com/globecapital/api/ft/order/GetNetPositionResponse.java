package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetNetPositionResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected GetNetPositionObject responseObject;

	public GetNetPositionObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(GetNetPositionObject responseObject) {
		this.responseObject = responseObject;
	}
	
}
