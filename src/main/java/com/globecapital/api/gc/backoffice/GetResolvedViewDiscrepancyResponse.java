package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetResolvedViewDiscrepancyResponse extends GCApiResponse {

	@SerializedName("Details")
	protected List<GetResolvedViewDiscrepancyRow> Details;

	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String message;

	@SerializedName("MethodName")
	protected String MethodName;
	
	@SerializedName("Totalrecords")
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

	public List<GetResolvedViewDiscrepancyRow> getDetails() {
		return Details;
	}

	public void setDetails(List<GetResolvedViewDiscrepancyRow> details) {
		Details = details;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}