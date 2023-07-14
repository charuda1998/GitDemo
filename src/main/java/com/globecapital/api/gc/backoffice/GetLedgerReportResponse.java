package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetLedgerReportResponse extends GCApiResponse {

	@SerializedName("Details")
	protected List<GetLedgerRows> Details;

	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String Msg;

	public List<GetLedgerRows> getDetails() {
		return Details;
	}

	public void setDetails(List<GetLedgerRows> details) {
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

}
