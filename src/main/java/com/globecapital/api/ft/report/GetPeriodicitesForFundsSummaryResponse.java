package com.globecapital.api.ft.report;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetPeriodicitesForFundsSummaryResponse extends FTResponse {
	
	
	@SerializedName("ResponseObject")
	protected GetPeriodicitesForFundsSummaryObject respObj;
	
	
	
	public GetPeriodicitesForFundsSummaryObject getResponseObject() {
		return respObj;
	}

	public void setResponseObject(GetPeriodicitesForFundsSummaryObject responseObject) {
		this.respObj = responseObject;
	}

	
	
}
