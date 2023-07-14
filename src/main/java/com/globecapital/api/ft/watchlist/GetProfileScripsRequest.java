package com.globecapital.api.ft.watchlist;

import static com.globecapital.api.ft.generics.FTConstants.IS_API_CALL;
import static com.globecapital.api.ft.generics.FTConstants.PROF_ID;
import static com.globecapital.api.ft.generics.FTConstants.PROF_NM;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetProfileScripsRequest extends FTRequest {
	
	public GetProfileScripsRequest() throws AppConfigNoKeyFoundException {
		super();
		setisApiCall(FTConstants.IS_APICALL);
	}
	public void setisApiCall(Boolean isApiCall) throws JSONException {
		addToData(IS_API_CALL, isApiCall);
	}
	public void setWatchlistId(String watchlistId) throws JSONException {
		addToData(PROF_ID, watchlistId);
	}
	public void setWatchlistName(String watchlistName) throws JSONException {
		addToData(PROF_NM, watchlistName);
	}
}
