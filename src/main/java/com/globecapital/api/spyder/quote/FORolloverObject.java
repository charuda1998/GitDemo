package com.globecapital.api.spyder.quote;

import com.google.gson.annotations.SerializedName;

public class FORolloverObject{
	
	@SerializedName("ScripCode")
	protected String sScripCode;
	
	@SerializedName("RolloverPercentage")
	protected String sRolloverPercentage;
	
	@SerializedName("Rollovercost")
	protected String sRollovercost;
	
	public String getScripCode() {
		return sScripCode;
	}

	public void setScripCode(String sScripCode) {
		this.sScripCode = sScripCode;
	}
	
	public String getRolloverPercentage() {
		return sRolloverPercentage;
	}

	public void setRolloverPercentage(String sRolloverPercentage) {
		this.sRolloverPercentage = sRolloverPercentage;
	}
	
	public String getRollovercost() {
		return sRollovercost;
	}

	public void setRollovercost(String sRollovercost) {
		this.sRollovercost = sRollovercost;
	}

}
