package com.globecapital.api.ft.report;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetAdvanceFundsViewResponse extends FTResponse {
	
	@SerializedName("ResponseObject")
	protected GetAdvanceFundsViewObject respObj;
	
	public GetAdvanceFundsViewObject getResponseObject() {
		return respObj;
	}

	public void setResponseObject(GetAdvanceFundsViewObject responseObject) {
		this.respObj = responseObject;
	}
}
