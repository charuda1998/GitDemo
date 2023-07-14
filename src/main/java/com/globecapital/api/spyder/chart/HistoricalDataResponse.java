package com.globecapital.api.spyder.chart;

import java.util.List;

import com.globecapital.api.spyder.generics.SpyderResponse;
import com.google.gson.annotations.SerializedName;

public class HistoricalDataResponse  extends SpyderResponse{
	
	@SerializedName("data")
	protected List<HistoricalDataObject> responseObject;

	public List<HistoricalDataObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<HistoricalDataObject> responseObject) {
		this.responseObject = responseObject;
	}

}
