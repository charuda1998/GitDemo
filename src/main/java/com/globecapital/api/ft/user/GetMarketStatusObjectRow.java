package com.globecapital.api.ft.user;

import com.google.gson.annotations.SerializedName;

public class GetMarketStatusObjectRow {

	@SerializedName("1")
	protected String sNSEMarketStatus;
	
	@SerializedName("2")
	protected String sNFOMarketStatus;

	@SerializedName("5")
	protected String sMCXMarketStatus;
	
	@SerializedName("13")
	protected String sNSECDSMarketStatus;
	
	public String getNSEMarketStatus() {
		return sNSEMarketStatus;
	}

	public void setNSEMarketStatus(String sNSEMarketStatus) {
		this.sNSEMarketStatus = sNSEMarketStatus;
	}

	public String getNFOMarketStatus() {
		return sNFOMarketStatus;
	}

	public void setNFOMarketStatus(String sNFOMarketStatus) {
		this.sNFOMarketStatus = sNFOMarketStatus;
	}
	
	public String getMCXMarketStatus() {
		return sMCXMarketStatus;
	}

	public void setMCXMarketStatus(String sMCXMarketStatus) {
		this.sMCXMarketStatus = sMCXMarketStatus;
	}
	
	public String getNSECDSMarketStatus() {
		return sNSECDSMarketStatus;
	}

	public void setNSECDSMarketStatus(String sNSECDSMarketStatus) {
		this.sNSECDSMarketStatus = sNSECDSMarketStatus;
	}
	
}
