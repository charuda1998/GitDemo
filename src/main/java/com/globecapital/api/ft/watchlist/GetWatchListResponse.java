package com.globecapital.api.ft.watchlist;

import java.util.List;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetWatchListResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected List<GetWatchListObject> responseObject;

	public List<GetWatchListObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<GetWatchListObject> responseObject) {
		this.responseObject = responseObject;
	}

}
