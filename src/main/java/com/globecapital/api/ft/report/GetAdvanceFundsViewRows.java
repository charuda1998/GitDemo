package com.globecapital.api.ft.report;

import com.google.gson.annotations.SerializedName;

public class GetAdvanceFundsViewRows {

	@SerializedName("sDescription")
	protected String sDescription;

	@SerializedName("nTrading")
	protected float nTrading;

	
	public String getDescription() {
		return sDescription;
	}

	public void setDescription(String sDescription) {
		this.sDescription = sDescription;
	}

	public float getnTrading() {
		return nTrading;
	}

	public void setnTrading(float nTrading) {
		this.nTrading = nTrading;
	}
	
}
