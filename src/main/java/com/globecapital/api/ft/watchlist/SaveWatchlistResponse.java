package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class SaveWatchlistResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected CreateWatchlistObj responseObject;

	public CreateWatchlistObj getResponseObject() {
		return responseObject;
	}

	public void setResponseObejct(CreateWatchlistObj responseObject) {
		this.responseObject = responseObject;
	}

}
