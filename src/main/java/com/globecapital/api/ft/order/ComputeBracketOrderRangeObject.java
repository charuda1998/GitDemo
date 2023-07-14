package com.globecapital.api.ft.order;

import com.google.gson.annotations.SerializedName;

public class ComputeBracketOrderRangeObject {
	
	@SerializedName("Status")
	protected String sStatus;

	@SerializedName("Desc")
	protected String sDesc;

	@SerializedName("Limit Price Low")
	protected String sLimitPriceLow;

	@SerializedName("Limit Price High")
	protected String sLimitPriceHigh;

	@SerializedName("Trigger Price Low")
	protected String sTriggerPriceLow;
	
	@SerializedName("Trigger Price High")
	protected String sTriggerPriceHigh;
	
	@SerializedName("Profit Price Low")
	protected String sProfitPriceLow;
	
	@SerializedName("Profit Price High")
	protected String sProfitPriceHigh;
	
	@SerializedName("Overall Price Low")
	protected String sOverallPriceLow;
	
	@SerializedName("Overall Price High")
	protected String sOverallPriceHigh;

	public String getStatus() {
		return sStatus;
	}

	public void setStatus(String sStatus) {
		this.sStatus = sStatus;
	}

	public String getDesc() {
		return sDesc;
	}

	public void setDesc(String sDesc) {
		this.sDesc = sDesc;
	}

	public String getLimitPriceLow() {
		return sLimitPriceLow;
	}

	public void setLimitPriceLow(String sLimitPriceLow) {
		this.sLimitPriceLow = sLimitPriceLow;
	}

	public String getLimitPriceHigh() {
		return sLimitPriceHigh;
	}

	public void setLimitPriceHigh(String sLimitPriceHigh) {
		this.sLimitPriceHigh = sLimitPriceHigh;
	}
	
	public String getTriggerPriceLow() {
		return sTriggerPriceLow;
	}

	public void setTriggerPriceLow(String sTriggerPriceLow) {
		this.sTriggerPriceLow = sTriggerPriceLow;
	}
	
	public String getTriggerPriceHigh() {
		return sTriggerPriceHigh;
	}

	public void setTriggerPriceHigh(String sTriggerPriceHigh) {
		this.sTriggerPriceHigh = sTriggerPriceHigh;
	}
	
	public String getProfitPriceLow() {
		return sProfitPriceLow;
	}

	public void setProfitPriceLow(String sProfitPriceLow) {
		this.sProfitPriceLow = sProfitPriceLow;
	}
	
	public String getProfitPriceHigh() {
		return sProfitPriceHigh;
	}

	public void setProfitPriceHigh(String sProfitPriceHigh) {
		this.sProfitPriceHigh = sProfitPriceHigh;
	}
	
	public String getOverallPriceLow() {
		return sOverallPriceLow;
	}

	public void setOverallPriceLow(String sOverallPriceLow) {
		this.sOverallPriceLow = sOverallPriceLow;
	}
	
	public String getOverallPriceHigh() {
		return sOverallPriceHigh;
	}

	public void setOverallPriceHigh(String sOverallPriceHigh) {
		this.sOverallPriceHigh = sOverallPriceHigh;
	}
	


}
