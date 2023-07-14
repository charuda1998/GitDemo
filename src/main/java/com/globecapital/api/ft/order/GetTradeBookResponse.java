package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetTradeBookResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected GetTradeBookResponseObject responseObject;

	public GetTradeBookResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(GetTradeBookResponseObject responseObject) {
		this.responseObject = responseObject;
	}

}
