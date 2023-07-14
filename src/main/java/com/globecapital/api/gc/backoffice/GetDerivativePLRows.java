package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetDerivativePLRows {

	@SerializedName("BQTY")
	protected String buyQty;

	@SerializedName("SQTY")
	protected String sellQty;

	@SerializedName("Scripname")
	protected String scripname;

	@SerializedName("RLPL")
	protected String realizedPL;

	@SerializedName("BAR")
	protected String buyAvg;

	@SerializedName("SVAL")
	protected String sellValue;

	@SerializedName("SAR")
	protected String sellAvg;

	@SerializedName("BVAL")
	protected String buyValue;

	public String getBuyQty() {
		return buyQty;
	}

	public void setBuyQty(String buyQty) {
		this.buyQty = buyQty;
	}

	public String getSellQty() {
		return sellQty;
	}

	public void setSellQty(String sellQty) {
		this.sellQty = sellQty;
	}

	public String getScripname() {
		return scripname;
	}

	public void setScripname(String scripname) {
		this.scripname = scripname;
	}

	public String getRealizedPL() {
		return realizedPL;
	}

	public void setRealizedPL(String realizedPL) {
		this.realizedPL = realizedPL;
	}

	public String getBuyAvg() {
		return buyAvg;
	}

	public void setBuyAvg(String buyAvg) {
		this.buyAvg = buyAvg;
	}

	public String getSellValue() {
		return sellValue;
	}

	public void setSellValue(String sellValue) {
		this.sellValue = sellValue;
	}

	public String getSellAvg() {
		return sellAvg;
	}

	public void setSellAvg(String sellAvg) {
		this.sellAvg = sellAvg;
	}

	public String getBuyValue() {
		return buyValue;
	}

	public void setBuyValue(String buyValue) {
		this.buyValue = buyValue;
	}

}
