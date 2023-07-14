package com.globecapital.api.ft.watchlist;

import com.google.gson.annotations.SerializedName;

public class GetSymbolWatchlistObjTable2 {
	
	@SerializedName("ProfId")
	protected String profId;
	
	@SerializedName("ProfNm")
	protected String profNm;
	
	@SerializedName("NoOfRec")
	protected String noOfRec;
	
	@SerializedName("ProfTempId")
	protected String profTempId;
	
	@SerializedName("sColumnPreference")
	protected String sColumnPreference;
	
	@SerializedName("cIsDefault")
	protected String cIsDefault;
	
	@SerializedName("sSortByColumn")
	protected String sSortByColumn;
	
	@SerializedName("sColumnFreezeTill")
	protected String sColumnFreezeTill;
	
	@SerializedName("sDefaultColumnPreference")
	protected String sDefaultColumnPreference;
	
	@SerializedName("sPivotColumnPreference")
	protected String sPivotColumnPreference;

	public String getProfId() {
		return profId;
	}

	public void setProfId(String profId) {
		this.profId = profId;
	}

	public String getProfNm() {
		return profNm;
	}

	public void setProfNm(String profNm) {
		this.profNm = profNm;
	}

	public String getNoOfRec() {
		return noOfRec;
	}

	public void setNoOfRec(String noOfRec) {
		this.noOfRec = noOfRec;
	}

	public String getProfTempId() {
		return profTempId;
	}

	public void setProfTempId(String profTempId) {
		this.profTempId = profTempId;
	}

	public String getsColumnPreference() {
		return sColumnPreference;
	}

	public void setsColumnPreference(String sColumnPreference) {
		this.sColumnPreference = sColumnPreference;
	}

	public String getcIsDefault() {
		return cIsDefault;
	}

	public void setcIsDefault(String cIsDefault) {
		this.cIsDefault = cIsDefault;
	}

	public String getsSortByColumn() {
		return sSortByColumn;
	}

	public void setsSortByColumn(String sSortByColumn) {
		this.sSortByColumn = sSortByColumn;
	}

	public String getsColumnFreezeTill() {
		return sColumnFreezeTill;
	}

	public void setsColumnFreezeTill(String sColumnFreezeTill) {
		this.sColumnFreezeTill = sColumnFreezeTill;
	}

	public String getsDefaultColumnPreference() {
		return sDefaultColumnPreference;
	}

	public void setsDefaultColumnPreference(String sDefaultColumnPreference) {
		this.sDefaultColumnPreference = sDefaultColumnPreference;
	}

	public String getsPivotColumnPreference() {
		return sPivotColumnPreference;
	}

	public void setsPivotColumnPreference(String sPivotColumnPreference) {
		this.sPivotColumnPreference = sPivotColumnPreference;
	}
	

	
}
