package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class ComputeBracketOrderRangeResponse extends FTResponse{
	
	@SerializedName("ResponseObject")
	protected ComputeBracketOrderRangeObject responseObject;

	public ComputeBracketOrderRangeObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(ComputeBracketOrderRangeObject responseObject) {
		this.responseObject = responseObject;
	}

}
