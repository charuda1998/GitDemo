package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class GetSymbolWatchlistAPI extends FTApi<GetProfileScripsRequest, GetProfileScripsResponse> {

	public GetSymbolWatchlistAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getProfileScrips"));
	}

}
