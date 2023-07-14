package com.globecapital.api.ft.user;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetMarketStatusResponseObject {
	
	@SerializedName("MarketStatus")
	protected List<GetMarketStatusObjectRow> MarketStatus;


	public List<GetMarketStatusObjectRow> getObjJSONRows() {
		return MarketStatus;
	}

	public void setObjJSONRows(List<GetMarketStatusObjectRow> MarketStatus) {
		this.MarketStatus = MarketStatus;
	}

}
