package com.globecapital.api.spyder.quote;

import com.google.gson.annotations.SerializedName;

public class CurrencyAPIObject {
	
	@SerializedName("ScripCode")
	protected String sScripCode;
	
	@SerializedName("OI")
	protected String sOI;
	
	@SerializedName("SpotPrice")
	protected String sSpotPrice;
		
	@SerializedName("OIChange")
	protected String sOIChange;
	
	@SerializedName("Rollovercost")
	protected String sRollovercost;
	
	@SerializedName("RolloverPercentage")
	protected String sRolloverPercentage;
	
	public String getScripCode() {
		return sScripCode;
	}

	public void setScripCode(String sScripCode) {
		this.sScripCode = sScripCode;
	}
	
	public String getOI() {
		return sOI;
	}

	public void setOI(String sOI) {
		this.sOI = sOI;
	}
	
	public String getSpotPrice() {
		return sSpotPrice;
	}

	public void setSpotPrice(String sSpotPrice) {
		this.sSpotPrice = sSpotPrice;
	}
		
	public String getOIChange() {
		return sOIChange;
	}

	public void setOIChange(String sOIChange) {
		this.sOIChange = sOIChange;
	}
	
	public String getRollovercost() {
		return sRollovercost;
	}

	public void setRollovercost(String sRollovercost) {
		this.sRollovercost = sRollovercost;
	}
	
	public String getRolloverPercentage() {
		return sRolloverPercentage;
	}

	public void setRolloverPercentage(String sRolloverPercentage) {
		this.sRolloverPercentage = sRolloverPercentage;
	}
	
}
