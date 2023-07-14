package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class LogOffResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected Object responseObject;

	public Object getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(Object responseObject) {
		this.responseObject = responseObject;
	}

}
