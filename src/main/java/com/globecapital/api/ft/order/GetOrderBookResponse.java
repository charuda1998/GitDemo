package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTResponse;
import com.google.gson.annotations.SerializedName;

public class GetOrderBookResponse extends FTResponse{
	
	@SerializedName("ResponseObject")
	protected GetOrderBookResponseObject respObj;
	
	public GetOrderBookResponseObject getResponseObject() {
		return respObj;
	}

	public void setResponseObject(GetOrderBookResponseObject responseObject) {
		this.respObj = responseObject;
	}

}

