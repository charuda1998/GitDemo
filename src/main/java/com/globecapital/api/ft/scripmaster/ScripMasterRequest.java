package com.globecapital.api.ft.scripmaster;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class ScripMasterRequest extends FTRequest {

	public ScripMasterRequest() throws AppConfigNoKeyFoundException {
		super();
	}

	public void setMKtSegId(int MKtSegId) throws JSONException {
		addToData(FTConstants.MKT_SEGID, MKtSegId);
	}

}
