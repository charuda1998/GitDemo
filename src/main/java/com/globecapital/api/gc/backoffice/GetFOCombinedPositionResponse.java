package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetFOCombinedPositionResponse  extends GCApiResponse {

	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;
	
	@SerializedName("MethodName")
	protected String methodName;
	
	@SerializedName("TotHoldingValue")
	protected String totHoldingValue;
	
	@SerializedName("TotRecords")
	protected String totRecords;

	@SerializedName("Details")
	protected List<GetFOCombinedPositionRows> Details;

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
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getTotHoldingValue() {
		return totHoldingValue;
	}

	public void setTotHoldingValue(String totHoldingValue) {
		this.totHoldingValue = totHoldingValue;
	}
	
	public String getTotRecords() {
		return totRecords;
	}

	public void setTotRecords(String totRecords) {
		this.totRecords = totRecords;
	}

	public List<GetFOCombinedPositionRows> getDetails() {
		return Details;
	}

	public void setDetails(List<GetFOCombinedPositionRows> details) {
		Details = details;
	}
	

}