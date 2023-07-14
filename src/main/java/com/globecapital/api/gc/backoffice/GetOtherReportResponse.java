package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetOtherReportResponse extends GCApiResponse {

	@SerializedName("PLIST")
	protected List<GetPeriodDates> dates;

	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String Msg;

	public List<GetPeriodDates> getDates() {
		return dates;
	}

	public void setDates(List<GetPeriodDates> dates) {
		this.dates = dates;
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