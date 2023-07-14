package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetTransactionResponse extends GCApiResponse {

	@SerializedName("TradeDetails")
	protected List<GetTransactionRows> TradeDetails;

	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String Msg;

	public List<GetTransactionRows> getTradeDetails() {
		return TradeDetails;
	}

	public void setTradeDetails(List<GetTransactionRows> tradeDetails) {
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

}
