package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetTransactionScripResponse extends GCApiResponse {

	@SerializedName("Details")
	protected List<GetTransactionScripRows> Details;

	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;

	public List<GetTransactionScripRows> getDetails() {
		return Details;
	}

	public void setDetails(List<GetTransactionScripRows> details) {
		Details = details;
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

}
