package com.globecapital.api.ft.user;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class SetAuthenticationStatusResponse extends FTResponse{
	
	@SerializedName("ResponseObject")
	protected SetAuthStatusResponseObject respObj;
	
	public SetAuthStatusResponseObject getResponseObject() {
		return respObj;
	}

	public void setResponseObject(SetAuthStatusResponseObject responseObject) {
		this.respObj = responseObject;
	}

}
