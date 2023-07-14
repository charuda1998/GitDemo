package com.globecapital.api.spyder.market;

import java.util.List;

import com.globecapital.api.spyder.generics.SpyderResponse;
import com.google.gson.annotations.SerializedName;

public class MarketMoversResponse extends SpyderResponse {
	
	@SerializedName("DateTime")
	protected String dateTime;
	
	public String getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	@SerializedName("data")
	protected List<MarketMoversObject> responseObject;

	public List<MarketMoversObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<MarketMoversObject> responseObject) {
		this.responseObject = responseObject;
	}
	
	
}
