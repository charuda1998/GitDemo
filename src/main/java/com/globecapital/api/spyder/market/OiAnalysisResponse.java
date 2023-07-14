package com.globecapital.api.spyder.market;

import java.util.List;

import com.globecapital.api.spyder.generics.SpyderResponse;
import com.google.gson.annotations.SerializedName;

public class OiAnalysisResponse extends SpyderResponse {
	
	@SerializedName("data")
	protected List<OiAnalysisObject> responseObject;

	public List<OiAnalysisObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<OiAnalysisObject> responseObject) {
		this.responseObject = responseObject;
	}

}
