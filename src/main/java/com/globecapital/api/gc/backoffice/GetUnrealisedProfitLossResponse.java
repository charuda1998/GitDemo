package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetUnrealisedProfitLossResponse extends GCApiResponse {

	@SerializedName("Detail")
	protected List<GetUnrealisedPLRows> TradeDetails;

	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String Msg;

	@SerializedName("totInvestvalue")
	protected String totalInvestmentValue;

	@SerializedName("totMktvalue")
	protected String totalMarketValue;

	@SerializedName("totUnrelpl")
	protected String totalUnRelPl;
	
	@SerializedName("Date")
	protected String date;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<GetUnrealisedPLRows> getTradeDetails() {
		return TradeDetails;
	}

	public void setTradeDetails(List<GetUnrealisedPLRows> tradeDetails) {
		TradeDetails = tradeDetails;
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

	public String getTotalInvestmentValue() {
		return totalInvestmentValue;
	}

	public void setTotalInvestmentValue(String totalInvestmentValue) {
		this.totalInvestmentValue = totalInvestmentValue;
	}

	public String getTotalMarketValue() {
		return totalMarketValue;
	}

	public void setTotalMarketValue(String totalMarketValue) {
		this.totalMarketValue = totalMarketValue;
	}

	public String getTotalUnRelPl() {
		return totalUnRelPl;
	}

	public void setTotalUnRelPl(String totalUnRelPl) {
		this.totalUnRelPl = totalUnRelPl;
	}

}