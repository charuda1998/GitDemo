package com.globecapital.api.spyder.quote;

import java.util.List;

import com.globecapital.api.spyder.generics.SpyderResponse;
import com.google.gson.annotations.SerializedName;

public class FORolloverResponse  extends SpyderResponse{
	
	@SerializedName("data")
	protected List<FORolloverObject> responseObject;

	public List<FORolloverObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<FORolloverObject> responseObject) {
		this.responseObject = responseObject;
	}

}
