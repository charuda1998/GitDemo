package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetPartyDetailsResponse extends GCApiResponse {

	@SerializedName("PAN")
	protected String PAN;

	@SerializedName("Status")
	protected Boolean Status;

	@SerializedName("Msg")
	protected String message;

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

	public String getPAN() {
		return PAN;
	}

	public void setPAN(String pAN) {
		PAN = pAN;
	}

}