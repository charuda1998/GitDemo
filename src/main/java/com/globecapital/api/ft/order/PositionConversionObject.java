package com.globecapital.api.ft.order;

import com.google.gson.annotations.SerializedName;

public class PositionConversionObject {
	
	@SerializedName("Status")
	protected String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
