package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class RenameWatchlistAPI extends FTApi<FTRequest, RenameWatchlistResponse> {
	public RenameWatchlistAPI() throws GCException {
		super(AppConfig.getValue("moon.api.renameWatchlist"));
	}
}