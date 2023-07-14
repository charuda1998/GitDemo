package com.globecapital.api.spyder.market;

import com.google.gson.annotations.SerializedName;

public class RolloverAnalysisObject {
	
	protected String ScCode;
	
	@SerializedName("RolloverC")
	protected String rollovercost;
	
	@SerializedName("RolloverCP")
	protected String rollOverCostPer;
	
	@SerializedName("RolloverP")
	protected String rolOverPer;

	public String getCode() {
		return ScCode;
	}
	public void setCode(String code) {
		this.ScCode = code;
	}
	
	public String getRolloverCost() {
		return rollovercost;
	}

	public void setRolloverCost(String rolloverCost) {
		this.rollovercost = rolloverCost;
	}
	
	public String getRolloverCostPer() {
		return rollOverCostPer;
	}

	public void setRolloverCostPer(String rolloverCostPer) {
		this.rollOverCostPer = rolloverCostPer;
	}
	
	public String getRolloverPer() {
		return rolOverPer;
	}

	public void setRolloverPer(String rolloverPer) {
		this.rolOverPer = rolloverPer;
	}
	
}
