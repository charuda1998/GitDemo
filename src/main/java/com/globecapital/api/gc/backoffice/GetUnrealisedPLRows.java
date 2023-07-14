package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetUnrealisedPLRows {

	@SerializedName("Sname")
	protected String scripname;

	@SerializedName("Unrelpl")
	protected String unRealizedPL;

	@SerializedName("Avgprice")
	protected String avgPrice;

	@SerializedName("OpenValue")
	protected String openValue;

	@SerializedName("Mktvalue")
	protected String marketValue;

	@SerializedName("qty")
	protected String openQty;

	@SerializedName("EXCHANGE")
	protected String exchange;

	@SerializedName("INSTYPE")
	protected String instrument;

	@SerializedName("SH_CODE")
	protected String scripCode;
	
	@SerializedName("ISIN")
	protected String isin;
	
	@SerializedName("LTP")
	protected String LTP;
	
	@SerializedName("discrepancy")
	protected String isDiscrepancy;
	
	public String getLTP() {
		return LTP;
	}

	public void setLTP(String LTP) {
		this.LTP = LTP;
	}
	
	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getScripCode() {
		return scripCode;
	}

	public void setScripCode(String scripCode) {
		this.scripCode = scripCode;
	}

	public String getOpenQty() {
		return openQty;
	}

	public void setOpenQty(String openQty) {
		this.openQty = openQty;
	}

	public String getScripname() {
		return scripname;
	}

	public void setScripname(String scripname) {
		this.scripname = scripname;
	}

	public String getUnRealizedPL() {
		return unRealizedPL;
	}

	public void setUnRealizedPL(String unRealizedPL) {
		this.unRealizedPL = unRealizedPL;
	}

	public String getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(String avgPrice) {
		this.avgPrice = avgPrice;
	}

	public String getOpenValue() {
		return openValue;
	}

	public void setOpenValue(String openValue) {
		this.openValue = openValue;
	}

	public String getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(String marketValue) {
		this.marketValue = marketValue;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getIsDiscrepancy() {
		return isDiscrepancy;
	}

	public void setIsDiscrepancy(String isDiscrepancy) {
		this.isDiscrepancy = isDiscrepancy;
	}

}
