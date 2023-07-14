package com.globecapital.api.ft.margin;

import com.globecapital.api.ft.esip.FtEQsipResponse;
import com.google.gson.annotations.SerializedName;

public class MarginInfoResponse extends FtEQsipResponse {

	@SerializedName("ResponseObject")
	protected MarginInfoResponseObject responseObject;
	
	public MarginInfoResponseObject getResponseObject() {
		return responseObject;
	}


}
