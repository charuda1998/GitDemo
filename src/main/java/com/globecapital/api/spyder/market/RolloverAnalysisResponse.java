package com.globecapital.api.spyder.market;

import java.util.List;

import com.globecapital.api.spyder.generics.SpyderResponse;
import com.google.gson.annotations.SerializedName;

public class RolloverAnalysisResponse extends SpyderResponse {
	
	@SerializedName("data")
	protected List<RolloverAnalysisObject> responseObject;

	public List<RolloverAnalysisObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<RolloverAnalysisObject> responseObject) {
		this.responseObject = responseObject;
	}
}
