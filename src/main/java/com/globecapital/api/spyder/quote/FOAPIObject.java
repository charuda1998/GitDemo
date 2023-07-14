package com.globecapital.api.spyder.quote;

import com.google.gson.annotations.SerializedName;

public class FOAPIObject{
	
	@SerializedName("ScripCode")
	protected String sScripCode;
	
	@SerializedName("OI")
	protected String sOI;
	
	@SerializedName("SpotPrice")
	protected String sSpotPrice;
	
	@SerializedName("PremiumPercentage")
	protected String sPremiumPercentage;
	
	@SerializedName("OIChange")
	protected String sOIChange;
	
	@SerializedName("OIChangePercentage")
	protected String sOIChangePercentage;
	
	@SerializedName("PCR")
	protected String sPCR;
	
	@SerializedName("IV")
	protected String sIV;
	
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
	
	public String getPremiumPercentage() {
		return sPremiumPercentage;
	}

	public void setPremiumPercentage(String sPremiumPercentage) {
		this.sPremiumPercentage = sPremiumPercentage;
	}
	
	public String getOIChange() {
		return sOIChange;
	}

	public void setOIChange(String sOIChange) {
		this.sOIChange = sOIChange;
	}
	
	
	public String getOIChangePercentage() {
		return sOIChangePercentage;
	}

	public void setOIChangePercentage(String sOIChangePercentage) {
		this.sOIChangePercentage = sOIChangePercentage;
	}
	
	
	public String getPCR() {
		return sPCR;
	}

	public void setPCR(String sPCR) {
		this.sPCR = sPCR;
	}
	
	
	public String getIV() {
		return sIV;
	}

	public void setIV(String sIV) {
		this.sIV = sIV;
	}
	

}
