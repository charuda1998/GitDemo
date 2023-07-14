package com.globecapital.api.ft.OMEX;

import java.util.List;

import com.globecapital.api.ft.generics.FTResponse;

public class AuthenticateResponse extends FTResponse{
	
	protected List<AuthenticateObj> responseObject;

	public List<AuthenticateObj> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<AuthenticateObj> responseObject) {
		this.responseObject = responseObject;
	}
}
