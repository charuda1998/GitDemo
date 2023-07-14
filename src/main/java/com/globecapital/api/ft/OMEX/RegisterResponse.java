package com.globecapital.api.ft.OMEX;

import java.util.List;

import com.globecapital.api.ft.generics.FTResponse;

public class RegisterResponse extends FTResponse{
	
	protected List<RegisterObject> responseObject;

	public List<RegisterObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<RegisterObject> responseObject) {
		this.responseObject = responseObject;
	}

}
