package com.globecapital.api.ft.margin;

import com.google.gson.annotations.SerializedName;

public class MarginInfoResponseObject {


	@SerializedName("ApproxMargin")
	protected String approxMargin;

	@SerializedName("AvailableMargin")
	protected String availableMargin;
	
	@SerializedName("Brokerage")
	protected String brokerage;

	public String getApproxMargin() {
		return approxMargin;
	}

	public String getAvailableMargin() {
		return availableMargin;
	}
	
	public String getBrokerage() {
		return brokerage;
	}
}
