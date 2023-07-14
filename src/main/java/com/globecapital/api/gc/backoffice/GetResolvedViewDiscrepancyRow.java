package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetResolvedViewDiscrepancyRow {

	@SerializedName("Adddatetime")
	protected String sAdddatetime;

	@SerializedName("BUYSELL")
	protected String sBuySell;
	
	@SerializedName("QTY")
	protected String sQty;
	
	@SerializedName("RATE")
	protected String sRate;

	@SerializedName("REFNO")
	protected String sRefNo;
	
	@SerializedName("REMARKS")
	protected String sRemarks;
	
	@SerializedName("SH_CODE")
	protected String sScripCode;
	
	@SerializedName("SH_NAME")
	protected String sScripName;
	
	@SerializedName("TRXDATE")
	protected String sTrxDate;
	

	public String getAdddatetime() {
		return sAdddatetime;
	}

	public void setAdddatetime(String sAdddatetime) {
		this.sAdddatetime = sAdddatetime;
	}

	public String getBuySell() {
		return sBuySell;
	}

	public void setBuySell(String sBuySell) {
		this.sBuySell = sBuySell;
	}

	public String getQty() {
		return sQty;
	}

	public void setQty(String sQty) {
		this.sQty = sQty;
	}
	
	public String getRate() {
		return sRate;
	}

	public void setRate(String sRate) {
		this.sRate = sRate;
	}
	
	public String getRefNo() {
		return sRefNo;
	}

	public void setRefNo(String sRefNo) {
		this.sRefNo = sRefNo;
	}
	
	public String getRemarks() {
		return sRemarks;
	}

	public void setRemarks(String sRemarks) {
		this.sRemarks = sRemarks;
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
	
	public String getTrxDate() {
		return sTrxDate;
	}

	public void setTrxDate(String sTrxDate) {
		this.sTrxDate = sTrxDate;
	}

}
