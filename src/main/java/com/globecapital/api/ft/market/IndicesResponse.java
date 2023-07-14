package com.globecapital.api.ft.market;

import java.util.List;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class IndicesResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected List<IndicesObj> responseObject;

	public List<IndicesObj> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<IndicesObj> responseObject) {
		this.responseObject = responseObject;
	}
}
