package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class SendOrdReqResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected SendOrdReqObject responseObject;

	public SendOrdReqObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(SendOrdReqObject responseObject) {
		this.responseObject = responseObject;
	}

}
