package com.globecapital.api.ft.report;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GetPeriodicitesForFundsSummaryObject {

	

	@SerializedName("PeriodicityName")
	protected String sPeriodicityName;

	@SerializedName("nPeriodicity")
	protected long nPeriodicity;

	@SerializedName("dtPeriodicities")
	protected List<GetPeriodicitesForFundsSummaryRows> responseObject;
	
	public String getPeriodicityName() {
		return sPeriodicityName;
	}

	public void setPeriodicityName(String sPeriodicityName) {
		this.sPeriodicityName = sPeriodicityName;
	}

	public long getnPeriodicity() {
		return nPeriodicity;
	}

	public void setnPeriodicity(long nPeriodicity) {
		this.nPeriodicity = nPeriodicity;
	}
	
	public List<GetPeriodicitesForFundsSummaryRows> getListResponseObject() {
		return responseObject;
	}

	public void setListResponseObject(List<GetPeriodicitesForFundsSummaryRows> responseObject) {
		this.responseObject = responseObject;
	}
	
}
