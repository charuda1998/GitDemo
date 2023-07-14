package com.globecapital.api.ft.market;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetExchangeMessagesResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected GetExchangeMessagesResponseObject responseObject;

	public GetExchangeMessagesResponseObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(GetExchangeMessagesResponseObject responseObject) {
		this.responseObject = responseObject;
	}
}
