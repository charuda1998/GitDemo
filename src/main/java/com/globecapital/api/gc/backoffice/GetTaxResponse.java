package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetTaxResponse extends GCApiResponse {

	@SerializedName("Details")
	protected List<GetTaxRows> details;

	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String Msg;

	@SerializedName("TAXRATE")
	protected String totalTaxRate;

	@SerializedName("TOTPL")
	protected String totalPL;

	@SerializedName("totbuy")
	protected String totalBuy;

	@SerializedName("finyear")
	protected String financialYear;

	@SerializedName("totcharges")
	protected String totalCharges;

	@SerializedName("totsell")
	protected String totalSell;

	@SerializedName("tottaxamt")
	protected String totalTaxAmount;

	@SerializedName("INTRADAY_TAXRATE")
	protected String totalIntradayPercent;
	
	@SerializedName("INTRADAY_TOTPL")
	protected String totalIntradayPnL;
	
	@SerializedName("INTRADAY_TOTTAX")
	protected String totalIntradayTax;
	
	@SerializedName("ST_TAXRATE")
	protected String totalShortTermPercent;
	
	@SerializedName("LT_TAXRATE")
	protected String totalLongTermPercent;
	
	@SerializedName("ST_TOTPL")
	protected String totalShortTermPnL;

	@SerializedName("LT_TOTPL")
	protected String totalLongTermPnL;
	
	@SerializedName("ST_TOTTAX")
	protected String totalShortTermTax;
	
	@SerializedName("LT_TOTTAX")
	protected String totalLongTermTax;
	
	@SerializedName("totvalue")
	protected String totalValue;


	public List<GetTaxRows> getDetails() {
		return details;
	}

	public void setDetails(List<GetTaxRows> details) {
		this.details = details;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getMsg() {
		return Msg;
	}

	public void setMsg(String msg) {
		Msg = msg;
	}

	public String getTotalTaxRate() {
		return totalTaxRate;
	}

	public void setTotalTaxRate(String totalTaxRate) {
		this.totalTaxRate = totalTaxRate;
	}

	public String getTotalPL() {
		return totalPL;
	}

	public void setTotalPL(String totalPL) {
		this.totalPL = totalPL;
	}

	public String getTotalBuy() {
		return totalBuy;
	}

	public void setTotalBuy(String totalBuy) {
		this.totalBuy = totalBuy;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getTotalCharges() {
		return totalCharges;
	}

	public void setTotalCharges(String totalCharges) {
		this.totalCharges = totalCharges;
	}

	public String getTotalSell() {
		return totalSell;
	}

	public void setTotalSell(String totalSell) {
		this.totalSell = totalSell;
	}

	public String getTotalTaxAmount() {
		return totalTaxAmount;
	}

	public void setTotalTaxAmount(String totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}

	public String getTotalIntradayPercent() {
		return totalIntradayPercent;
	}

	public void setTotalIntradayPercent(String totalIntradayPercent) {
		this.totalIntradayPercent = totalIntradayPercent;
	}

	public String getTotalIntradayPnL() {
		return totalIntradayPnL;
	}

	public void setTotalIntradayPnL(String totalIntradayPnL) {
		this.totalIntradayPnL = totalIntradayPnL;
	}

	public String getTotalIntradayTax() {
		return totalIntradayTax;
	}

	public void setTotalIntradayTax(String totalIntradayTax) {
		this.totalIntradayTax = totalIntradayTax;
	}

	public String getTotalShortTermPercent() {
		return totalShortTermPercent;
	}

	public void setTotalShortTermPercent(String totalShortTermPercent) {
		this.totalShortTermPercent = totalShortTermPercent;
	}

	public String getTotalLongTermPercent() {
		return totalLongTermPercent;
	}

	public void setTotalLongTermPercent(String totalLongTermPercent) {
		this.totalLongTermPercent = totalLongTermPercent;
	}

	public String getTotalShortTermPnL() {
		return totalShortTermPnL;
	}

	public void setTotalShortTermPnL(String totalShortTermPnL) {
		this.totalShortTermPnL = totalShortTermPnL;
	}

	public String getTotalLongTermPnL() {
		return totalLongTermPnL;
	}

	public void setTotalLongTermPnL(String totalLongTermPnL) {
		this.totalLongTermPnL = totalLongTermPnL;
	}

	public String getTotalShortTermTax() {
		return totalShortTermTax;
	}

	public void setTotalShortTermTax(String totalShortTermTax) {
		this.totalShortTermTax = totalShortTermTax;
	}

	public String getTotalLongTermTax() {
		return totalLongTermTax;
	}

	public void setTotalLongTermTax(String totalLongTermTax) {
		this.totalLongTermTax = totalLongTermTax;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}

}