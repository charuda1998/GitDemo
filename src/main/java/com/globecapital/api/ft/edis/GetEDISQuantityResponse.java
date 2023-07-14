package com.globecapital.api.ft.edis;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetEDISQuantityResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected GetEDISQuantityResponseObject responseObject;

	public GetEDISQuantityResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(GetEDISQuantityResponseObject responseObject) {
		this.responseObject = responseObject;
	}
}
