package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetHoldingsResponse extends GCApiResponse {

	@SerializedName("Details")
	protected List<GetHoldingsRows> Details;

	@SerializedName("Status")
	protected Boolean Status;

	@SerializedName("Msg")
	protected String message;

	@SerializedName("TotHoldingValue")
	protected String TotHoldingValue;
	
	@SerializedName("Asondate")
	protected String asOnDate;
	
	public String getAsOnDate() {
		return asOnDate;
	}

	public void setAsOnDate(String asOnDate) {
		this.asOnDate = asOnDate;
	}

	public String getTotHoldingValue() {
		return TotHoldingValue;
	}

	public void setTotHoldingValue(String totHoldingValue) {
		TotHoldingValue = totHoldingValue;
	}

	public List<GetHoldingsRows> getDetails() {
		return Details;
	}

	public void setDetails(List<GetHoldingsRows> details) {
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