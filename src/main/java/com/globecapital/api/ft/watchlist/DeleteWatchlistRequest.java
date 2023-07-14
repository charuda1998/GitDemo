package com.globecapital.api.ft.watchlist;

import static com.globecapital.api.ft.generics.FTConstants.PROF_ID;
import static com.globecapital.api.ft.generics.FTConstants.PROF_NM;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class DeleteWatchlistRequest extends FTRequest {

	public DeleteWatchlistRequest() throws AppConfigNoKeyFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setWatchlistName(String watchlistName) throws JSONException {
		addToData(PROF_NM, watchlistName);
	}
	public void setWatchlistId(String watchlistId) throws JSONException {
		addToData(PROF_ID, watchlistId);
	}
}
