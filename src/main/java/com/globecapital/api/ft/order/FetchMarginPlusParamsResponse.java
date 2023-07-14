package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class FetchMarginPlusParamsResponse extends FTResponse{
	
	@SerializedName("ResponseObject")
	protected FetchMarginPlusParamsObject responseObject;

	public FetchMarginPlusParamsObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(FetchMarginPlusParamsObject responseObject) {
		this.responseObject = responseObject;
	}

}
