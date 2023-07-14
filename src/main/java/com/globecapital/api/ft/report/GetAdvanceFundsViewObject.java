package com.globecapital.api.ft.report;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetAdvanceFundsViewObject {

	@SerializedName("sDescription")
	protected String sDescription;

	@SerializedName("nTrading")
	protected float nTrading;
	

	@SerializedName("dtAdvanceFundsView")
	protected List<GetAdvanceFundsViewRows> responseObject;

	
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
	
	public List<GetAdvanceFundsViewRows> getListResponseObject() {
		return responseObject;
	}

	public void setListResponseObject(List<GetAdvanceFundsViewRows> responseObject) {
		this.responseObject = responseObject;
	}
	
}
