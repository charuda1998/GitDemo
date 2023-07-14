package com.globecapital.api.gc.backoffice;

import java.util.List;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class AMODetailResponse  extends GCApiResponse {

	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;
	
	@SerializedName("MethodName")
	protected String methodName;

	@SerializedName("Details")
	protected List<AMODetailRows> Details;

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

	public List<AMODetailRows> getDetails() {
		return Details;
	}

	public void setDetails(List<AMODetailRows> details) {
		Details = details;
	}
	

}