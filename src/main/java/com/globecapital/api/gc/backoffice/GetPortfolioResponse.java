package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetPortfolioResponse extends GCApiResponse {

	@SerializedName("Details")
	protected List<GetPortfolioRows> Details;

	@SerializedName("Asondate")
	protected String asOnDate;
	
	@SerializedName("Buyvalue")
	protected String totalPurchaseValue;

	@SerializedName("Marketvalue")
	protected String totalMarketValue;

	@SerializedName("Gainloss")
	protected String totalUnrealisedPnL;
	
	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String Msg;

	public List<GetPortfolioRows> getDetails() {
		return Details;
	}

	public void setDetails(List<GetPortfolioRows> details) {
		Details = details;
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

	public String getAsOnDate() {
		return asOnDate;
	}

	public void setAsOnDate(String asOnDate) {
		this.asOnDate = asOnDate;
	}

	public String getTotalPurchaseValue() {
		return totalPurchaseValue;
	}

	public void setTotalPurchaseValue(String totalPurchaseValue) {
		this.totalPurchaseValue = totalPurchaseValue;
	}

	public String getTotalMarketValue() {
		return totalMarketValue;
	}

	public void setTotalMarketValue(String totalMarketValue) {
		this.totalMarketValue = totalMarketValue;
	}

	public String getTotalUnrealisedPnL() {
		return totalUnrealisedPnL;
	}

	public void setTotalUnrealisedPnL(String totalUnrealisedPnL) {
		this.totalUnrealisedPnL = totalUnrealisedPnL;
	}

}
