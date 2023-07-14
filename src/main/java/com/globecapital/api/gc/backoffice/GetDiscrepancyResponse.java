package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetDiscrepancyResponse extends GCApiResponse {

	@SerializedName("Disdetails")
	protected List<GetDiscrepancyRow> Details;

	@SerializedName("Status")
	protected Boolean Status;

	@SerializedName("Msg")
	protected String message;

	@SerializedName("MethodName")
	protected String MethodName;
	
	@SerializedName("TotRecords")
	protected String TotRecords;
	
	public String getMethodName() {
		return MethodName;
	}

	public void setMethodName(String MethodName) {
		this.MethodName = MethodName;
	}

	public String getTotRecords() {
		return TotRecords;
	}

	public void setTotRecords(String TotRecords) {
		this.TotRecords = TotRecords;
	}

	public List<GetDiscrepancyRow> getDetails() {
		return Details;
	}

	public void setDetails(List<GetDiscrepancyRow> details) {
		Details = details;
	}

	public Boolean getStatus() {
		return Status;
	}

	public void setStatus(Boolean status) {
		Status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}