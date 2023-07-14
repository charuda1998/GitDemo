package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetRealisedDerivativesPLResponse extends GCApiResponse {

	@SerializedName("Details")
	protected List<GetRealisedPLRows> TradeDetails;

	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String Msg;

	@SerializedName("Totcharges")
	protected String totalCharges;

	@SerializedName("TOTRELPL")
	protected String totalRLPL;

	@SerializedName("NETRELPL")
	protected String netRLPL;

	public List<GetRealisedPLRows> getTradeDetails() {
		return TradeDetails;
	}

	public void setTradeDetails(List<GetRealisedPLRows> tradeDetails) {
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

	public String getTotalCharges() {
		return totalCharges;
	}

	public void setTotalCharges(String totalCharges) {
		this.totalCharges = totalCharges;
	}

	public String getTotalRLPL() {
		return totalRLPL;
	}

	public void setTotalRLPL(String totalRLPL) {
		this.totalRLPL = totalRLPL;
	}

	public String getNetRLPL() {
		return netRLPL;
	}

	public void setNetRLPL(String netRLPL) {
		this.netRLPL = netRLPL;
	}

}
