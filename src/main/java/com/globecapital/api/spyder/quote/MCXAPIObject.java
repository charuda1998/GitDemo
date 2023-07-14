package com.globecapital.api.spyder.quote;

import com.google.gson.annotations.SerializedName;

public class MCXAPIObject {
	
	@SerializedName("ScripCode")
	protected String sScripCode;
	
	@SerializedName("OI")
	protected String sOI;
	
	@SerializedName("SpotPrice")
	protected String sSpotPrice;
		
	@SerializedName("OIChange")
	protected String sOIChange;
	
	@SerializedName("OIChangePercentage")
	protected String sOIChangePercentage;
	
	@SerializedName("PremiumPercentage")
	protected String sPremiumPercentage;
	
	@SerializedName("Highcircuit")
	protected String sHighcircuit;
	
	@SerializedName("Lowcircuite")
	protected String sLowcircuite;
	
	@SerializedName("Contractstart")
	protected String sContractstart;
	
	@SerializedName("ContractEnd")
	protected String sContractEnd;
	
	@SerializedName("Tenderstart")
	protected String sTenderstart;
	
	@SerializedName("TenderEnd")
	protected String sTenderEnd;
	
	@SerializedName("Maxordersize")
	protected String sMaxordersize;
	
	@SerializedName("Deliveryunit")
	protected String sDeliveryunit;
	
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
		
	public String getOIChangePercentage() {
		return sOIChangePercentage;
	}
	
	public void setOIChangePercentage(String sOIChangePercentage) {
		this.sOIChangePercentage = sOIChangePercentage;
	}

	public void setOIChange(String sOIChange) {
		this.sOIChange = sOIChange;
	}
	
	public String getOIChange() {
		return sOIChange;
	}

	public String getPremiumPercentage() {
		return sPremiumPercentage;
	}

	public void setPremiumPercentage(String sPremiumPercentage) {
		this.sPremiumPercentage = sPremiumPercentage;
	}
	
	public String getHighcircuit() {
		return sHighcircuit;
	}

	public void setHighcircuit(String sHighcircuit) {
		this.sHighcircuit = sHighcircuit;
	}
	
	public String getLowcircuite() {
		return sLowcircuite;
	}

	public void setLowcircuite(String sLowcircuite) {
		this.sLowcircuite = sLowcircuite;
	}
	
	public String getContractstart() {
		return sContractstart;
	}

	public void setContractstart(String sContractstart) {
		this.sContractstart = sContractstart;
	}
	
	public String getContractEnd() {
		return sContractEnd;
	}

	public void setContractEnd(String sContractEnd) {
		this.sContractEnd = sContractEnd;
	}
	
	public String getTenderstart() {
		return sTenderstart;
	}

	public void setTenderstart(String sTenderstart) {
		this.sTenderstart = sTenderstart;
	}
	
	public String getTenderEnd() {
		return sTenderEnd;
	}

	public void setTenderEnd(String sTenderEnd) {
		this.sTenderEnd = sTenderEnd;
	}
	
	public String getMaxordersize() {
		return sMaxordersize;
	}

	public void setMaxordersize(String sMaxordersize) {
		this.sMaxordersize = sMaxordersize;
	}
	
	public String getDeliveryunit() {
		return sDeliveryunit;
	}

	public void setDeliveryunit(String sDeliveryunit) {
		this.sDeliveryunit = sDeliveryunit;
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
