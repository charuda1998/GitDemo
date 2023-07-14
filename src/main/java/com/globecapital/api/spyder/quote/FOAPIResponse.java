package com.globecapital.api.spyder.quote;

import java.util.List;

import com.globecapital.api.spyder.generics.SpyderResponse;
import com.google.gson.annotations.SerializedName;

public class FOAPIResponse  extends SpyderResponse{
	
	@SerializedName("data")
	protected List<FOAPIObject> responseObject;

	public List<FOAPIObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<FOAPIObject> responseObject) {
		this.responseObject = responseObject;
	}

}
