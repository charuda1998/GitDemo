package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetDiscrepancyRow {

	@SerializedName("DiffQTY")
	protected String sDiffQty;

	@SerializedName("ISIN")
	protected String sISIN;
	
	@SerializedName("ResolvedQTY")
	protected String sResolvedQty;
	
	@SerializedName("SCRIPCODE")
	protected String sScripCode;

	@SerializedName("SCRIPNAME")
	protected String sScripName;
	

	public String getDiffQty() {
		return sDiffQty;
	}

	public void setDiffQty(String sDiffQty) {
		this.sDiffQty = sDiffQty;
	}

	public String getISIN() {
		return sISIN;
	}

	public void setISIN(String sISIN) {
		this.sISIN = sISIN;
	}

	public String getResolvedQty() {
		return sResolvedQty;
	}

	public void setResolvedQty(String sResolvedQty) {
		this.sResolvedQty = sResolvedQty;
	}

	public String getScripCode() {
		return sScripCode;
	}

	public void setScripCode(String sScripCode) {
		this.sScripCode = sScripCode;
	}

	public String getScripName() {
		return sScripName;
	}

	public void setSCRIPNAME(String sScripName) {
		this.sScripName = sScripName;
	}

}
