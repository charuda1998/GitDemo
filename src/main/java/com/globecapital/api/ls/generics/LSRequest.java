package com.globecapital.api.ls.generics;

import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.api.generics.ApiRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class LSRequest extends ApiRequest  {
	
	public LSRequest() throws AppConfigNoKeyFoundException {
		super();
		this.respType = RESP_TYPE.JSON;
	}
	
	@Override
	public String toString() {
		return "";
	}

}
