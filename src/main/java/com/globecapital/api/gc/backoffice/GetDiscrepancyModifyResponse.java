package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetDiscrepancyModifyResponse extends GCApiResponse {

	@SerializedName("Status")
	protected String Status;

	@SerializedName("Msg")
	protected String message;

	@SerializedName("MethodName")
	protected String MethodName;
	
	public String getMethodName() {
		return MethodName;
	}

	public void setMethodName(String MethodName) {
		this.MethodName = MethodName;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		this.Status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}