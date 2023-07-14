package com.globecapital.api.ft.market;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class IndicesRequest extends FTRequest {

	public IndicesRequest() throws AppConfigNoKeyFoundException {
		super();
	} 

	public void setMKtSegId(int MKtSegId) throws JSONException {
		addToData(FTConstants.MKT_SEGID, MKtSegId);
	}
	public void setRecordCount(String RecordCount) throws JSONException {
		addToData(FTConstants.RECORD_COUNT, RecordCount);
	}
}
