package com.globecapital.api.ft.edis;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetEDISConfigResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected GetEDISConfigResponseObject responseObject;

	public GetEDISConfigResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(GetEDISConfigResponseObject responseObject) {
		this.responseObject = responseObject;
	}
}
