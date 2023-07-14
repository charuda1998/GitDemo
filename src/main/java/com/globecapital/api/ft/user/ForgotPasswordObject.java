package com.globecapital.api.ft.user;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordObject {
	
	@SerializedName("Status")
	protected String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}
