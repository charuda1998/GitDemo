package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class RenameWatchlistResponse extends FTResponse {

	@SerializedName("ResponseObject")

	protected RenameWatchlistObj responseObject;

	public RenameWatchlistObj getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(RenameWatchlistObj responseObject) {
		this.responseObject = responseObject;
	}

}
