package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class DeleteWatchlistResponse extends FTResponse {

	@SerializedName("ResponseObject")

	protected DeleteWatchlistObj responseObject;

	public DeleteWatchlistObj getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(DeleteWatchlistObj responseObject) {
		this.responseObject = responseObject;
	}

}
