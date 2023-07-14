package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetPortfolioRows {

	@SerializedName("MARKETVALUE")
	protected String marketValue;

	@SerializedName("SCRIPNAME")
	protected String scripname;

	@SerializedName("GAINLOSSS")
	protected String unrealisedPnL;

	@SerializedName("QTY")
	protected String qty;

	@SerializedName("ISIN")
	protected String isin;

	@SerializedName("AVR")
	protected String buyAvg;
	
	@SerializedName("discrepancy")
	protected String isDiscrepancy;

	public String getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(String marketValue) {
		this.marketValue = marketValue;
	}

	public String getScripname() {
		return scripname;
	}

	public void setScripname(String scripname) {
		this.scripname = scripname;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getBuyAvg() {
		return buyAvg;
	}

	public void setBuyAvg(String buyAvg) {
		this.buyAvg = buyAvg;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getUnrealisedPnL() {
		return unrealisedPnL;
	}

	public void setUnrealisedPnL(String unrealisedPnL) {
		this.unrealisedPnL = unrealisedPnL;
	}
	
	public String getIsDiscrepancy() {
		return isDiscrepancy;
	}

	public void setIsDiscrepancy(String isDiscrepancy) {
		this.isDiscrepancy = isDiscrepancy;
	}

}
