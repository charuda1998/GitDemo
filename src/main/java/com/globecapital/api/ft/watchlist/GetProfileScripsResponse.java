package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetProfileScripsResponse extends FTResponse {

	@SerializedName("ResponseObject")
	protected GetSymbolWatchlistObject responseObject;

	public GetSymbolWatchlistObject getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(GetSymbolWatchlistObject responseObject) {
		this.responseObject = responseObject;
	}
	
}
