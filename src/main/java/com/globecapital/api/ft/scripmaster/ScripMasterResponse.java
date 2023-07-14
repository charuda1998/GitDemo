package com.globecapital.api.ft.scripmaster;

import java.util.List;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class ScripMasterResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected List<ScripMasterObj> responseObject;

	public List<ScripMasterObj> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<ScripMasterObj> responseObject) {
		this.responseObject = responseObject;
	}
}
