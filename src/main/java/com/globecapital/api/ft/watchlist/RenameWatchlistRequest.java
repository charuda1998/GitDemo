package com.globecapital.api.ft.watchlist;

import static com.globecapital.api.ft.generics.FTConstants.PROF_ID;
import static com.globecapital.api.ft.generics.FTConstants.PROF_NM;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class RenameWatchlistRequest extends FTRequest {

	public RenameWatchlistRequest() throws AppConfigNoKeyFoundException {
		super();
		// TODO Auto-generated constructor stub
	}
	public void setWatchlistName(String watchlistName) throws JSONException {
		addToData(PROF_NM, watchlistName);
	}
	public void setWatchlistId(String watchlistId) throws JSONException {
		addToData(PROF_ID, watchlistId);
	}
	
	public void setNewWatchlistName(String newWatchlistName) throws JSONException {
		addToData(FTConstants.NEW_PROF_NM, newWatchlistName);
	}
}	