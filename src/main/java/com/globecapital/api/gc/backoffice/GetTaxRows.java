package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetTaxRows {

	@SerializedName("Sname")
	protected String scripName;

	@SerializedName("PL")
	protected String profitLoss;

	@SerializedName("TAXAMT")
	protected String taxAmount;

	@SerializedName("INTRADAY_PL")
	protected String intradayPL;

	@SerializedName("INTRADAY_TAXAMT")
	protected String intradyTaxAmount;

	@SerializedName("LT_PL")
	protected String longTermPL;

	@SerializedName("LT_TAXAMT")
	protected String longTermTaxAmount;

	@SerializedName("ST_PL")
	protected String shortTermPL;

	@SerializedName("ISIN")
	protected String isin;

	@SerializedName("ST_TAXAMT")
	protected String shortTermTaxAmount;

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getScripName() {
		return scripName;
	}

	public void setScripName(String scripName) {
		this.scripName = scripName;
	}

	public String getProfitLoss() {
		return profitLoss;
	}

	public void setProfitLoss(String profitLoss) {
		this.profitLoss = profitLoss;
	}

	public String getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getIntradayPL() {
		return intradayPL;
	}

	public void setIntradayPL(String intradayPL) {
		this.intradayPL = intradayPL;
	}

	public String getIntradyTaxAmount() {
		return intradyTaxAmount;
	}

	public void setIntradyTaxAmount(String intradyTaxAmount) {
		this.intradyTaxAmount = intradyTaxAmount;
	}

	public String getLongTermPL() {
		return longTermPL;
	}

	public void setLongTermPL(String longTermPL) {
		this.longTermPL = longTermPL;
	}

	public String getLongTermTaxAmount() {
		return longTermTaxAmount;
	}

	public void setLongTermTaxAmount(String longTermTaxAmount) {
		this.longTermTaxAmount = longTermTaxAmount;
	}

	public String getShortTermPL() {
		return shortTermPL;
	}

	public void setShortTermPL(String shortTermPL) {
		this.shortTermPL = shortTermPL;
	}

	public String getShortTermTaxAmount() {
		return shortTermTaxAmount;
	}

	public void setShortTermTaxAmount(String shortTermTaxAmount) {
		this.shortTermTaxAmount = shortTermTaxAmount;
	}

}
