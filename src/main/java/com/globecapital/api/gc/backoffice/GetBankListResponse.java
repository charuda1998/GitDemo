package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetBankListResponse  extends GCApiResponse {

	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;

	@SerializedName("DETAILS")
	protected List<GetBankListRows> Details;

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

	public List<GetBankListRows> getDetails() {
		return Details;
	}

	public void setDetails(List<GetBankListRows> details) {
		Details = details;
	}
	

}