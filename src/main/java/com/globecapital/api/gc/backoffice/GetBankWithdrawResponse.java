package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetBankWithdrawResponse extends GCApiResponse {

	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;
	
	@SerializedName("DISCLAIMER")
	protected String disclaimer;

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

	public String getDisclaimer() {
		return disclaimer;
	}
	
}