package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetGTDOrderBookResponse extends FTResponse{
	
	@SerializedName("ResponseObject")
	protected GetGTDOrderBookResponseObject respObj;
	
	public GetGTDOrderBookResponseObject getResponseObject() {
		return respObj;
	}

	public void setResponseObject(GetGTDOrderBookResponseObject responseObject) {
		this.respObj = responseObject;
	}

}

