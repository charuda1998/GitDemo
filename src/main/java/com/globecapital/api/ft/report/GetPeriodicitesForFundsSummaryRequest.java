package com.globecapital.api.ft.report;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.GCException;

public class GetPeriodicitesForFundsSummaryRequest extends FTRequest{

	public GetPeriodicitesForFundsSummaryRequest() throws JSONException, GCException {
		super();
		setApiCall(true);
	}
	
	public void setApiCall(boolean value) throws JSONException, GCException {
		addToData(FTConstants.API_CALL, value);
	}
	

}
