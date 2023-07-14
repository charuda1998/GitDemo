package com.globecapital.api.ft.watchlist;

import static com.globecapital.api.ft.generics.FTConstants.PROF_ID;
import static com.globecapital.api.ft.generics.FTConstants.PROF_NM;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class CreateWatchlistRequest extends FTRequest {
	private JSONArray symbolList;

	public CreateWatchlistRequest() throws AppConfigNoKeyFoundException {
		super();
		symbolList = new JSONArray();
	}

	public void setWatchlistId(String watchlistId) throws JSONException {
		addToData(PROF_ID, watchlistId);
	}

	public void setWatchlistName(String watchlistName) throws JSONException {
		addToData(PROF_NM, watchlistName);
	}

	public void addSymbols(String segmentId, String token) 
	{
		JSONObject data = new JSONObject();
		data.put(FTConstants.IS_GLOBAL, FTConstants.IS_GLOBAL_VALUE);
		data.put(FTConstants.TOKEN, token);
		data.put(FTConstants.MKT_SEG_ID, segmentId);
		symbolList.put(data);
		this.dataObj.put(FTConstants.MK_DET, symbolList);

	}

	public void addSymbols(List<String> tokenMKtSegIDList) {

		JSONArray symbolList = new JSONArray();
		for (String symbolToken : tokenMKtSegIDList) {
			String[] symTokenArr = symbolToken.split("_");
			JSONObject symTokenObj = new JSONObject();
			symTokenObj.put(FTConstants.IS_GLOBAL, FTConstants.IS_GLOBAL_VALUE);
			symTokenObj.put(FTConstants.TOKEN, symTokenArr[0]);
			symTokenObj.put(FTConstants.MKT_SEG_ID, symTokenArr[1]);
			symbolList.put(symTokenObj);
		}

		this.dataObj.put(FTConstants.MK_DET, symbolList);

	}

	/*** Watchlist creation with empty symbol list ***/
	public void addSymbols() 
	{
		JSONObject data = new JSONObject();
		data.put(FTConstants.IS_GLOBAL, FTConstants.IS_GLOBAL_VALUE);
		data.put(FTConstants.TOKEN, 0);
		data.put(FTConstants.MKT_SEG_ID, 0);
		symbolList.put(data);
		this.dataObj.put(FTConstants.MK_DET, symbolList);
	}

}
