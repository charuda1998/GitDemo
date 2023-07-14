package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class CreateWatchlistResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected CreateWatchlistObj responseObject;
	String message = "";

	public CreateWatchlistObj getResponseObject() {
		return responseObject;
	}

	public void setResponseObejct(CreateWatchlistObj responseObject) {
		this.responseObject = responseObject;
	}

}
