package com.globecapital.api.spyder.quote;

import java.util.List;

import com.globecapital.api.spyder.generics.SpyderResponse;
import com.google.gson.annotations.SerializedName;

public class MCXAPIResponse extends SpyderResponse{
	@SerializedName("data")
	protected List<MCXAPIObject> responseObject;

	public List<MCXAPIObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<MCXAPIObject> responseObject) {
		this.responseObject = responseObject;
	}

}
