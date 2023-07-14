package com.globecapital.api.ft.watchlist;

import static com.globecapital.api.ft.generics.FTConstants.SCRIP_COUNT;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetWatchlistRequest extends FTRequest {

	public GetWatchlistRequest() throws AppConfigNoKeyFoundException {
		super();
		setScripCount(FTConstants.SCRIPCOUNT);
	}

	public void setScripCount(Boolean scripCount) throws JSONException {
		addToData(SCRIP_COUNT, scripCount);
	}
}
