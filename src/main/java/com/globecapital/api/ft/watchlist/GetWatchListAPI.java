package com.globecapital.api.ft.watchlist;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;

public class GetWatchListAPI extends FTApi<FTRequest, GetWatchListResponse> {
	public GetWatchListAPI() throws GCException {
		super(AppConfig.getValue("moon.api.getProfileList"));
	}
}
