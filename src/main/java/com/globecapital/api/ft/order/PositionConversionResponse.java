package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class PositionConversionResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected PositionConversionObject responseObject;

	public PositionConversionObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObejct(PositionConversionObject responseObject) {
		this.responseObject = responseObject;
	}

}