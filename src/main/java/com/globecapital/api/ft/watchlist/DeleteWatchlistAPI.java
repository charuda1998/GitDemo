package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class DeleteWatchlistAPI extends FTApi<FTRequest, FTResponse> {

	public DeleteWatchlistAPI() throws GCException {
		super(AppConfig.getValue("moon.api.deleteWatchlistGroup"));
	}

}
