package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetFundHistoryResponse extends GCApiResponse {
	
	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;

	@SerializedName("Details")
	protected List<GetFundHistoryRows> Details;
	
	@SerializedName("Totalrecords")
	protected int totalRecords;
	
	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<GetFundHistoryRows> getDetails() {
		return Details;
	}

	public void setDetails(List<GetFundHistoryRows> details) {
		Details = details;
	}

}
