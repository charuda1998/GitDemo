package com.globecapital.api.ft.market;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetExchangeMessagesRequest extends FTRequest {

	public GetExchangeMessagesRequest() throws AppConfigNoKeyFoundException {
		super();
	} 

	public void setMktSegId(int sMKtSegId) throws JSONException {
		addToData(FTConstants.MKT_SEGID, sMKtSegId);
	}
	public void setHrs(int iHrs) throws JSONException {
		addToData(FTConstants.HRS, iHrs);
	}
}
