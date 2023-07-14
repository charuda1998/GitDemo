package com.globecapital.api.spyder.quote;

import java.util.List;

import com.globecapital.api.spyder.generics.SpyderResponse;
import com.google.gson.annotations.SerializedName;

public class CurrencyAPIResponse extends SpyderResponse{
	@SerializedName("data")
	protected List<CurrencyAPIObject> responseObject;

	public List<CurrencyAPIObject> getResponseObject() {
		return responseObject;
	}

	public void setResponseObject(List<CurrencyAPIObject> responseObject) {
		this.responseObject = responseObject;
	}

}
