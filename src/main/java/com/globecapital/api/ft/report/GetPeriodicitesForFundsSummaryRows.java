package com.globecapital.api.ft.report;


import com.google.gson.annotations.SerializedName;

public class GetPeriodicitesForFundsSummaryRows {

	

	@SerializedName("PeriodicityName")
	protected String sPeriodicityName;

	@SerializedName("nPeriodicity")
	protected long nPeriodicity;
	
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
	
	
}
