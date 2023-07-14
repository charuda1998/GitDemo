package com.globecapital.api.ft.esip;

import java.util.List;

import com.globecapital.api.generics.ApiResponse;
import com.google.gson.annotations.SerializedName;

public class FtEQsipResponse extends ApiResponse {

	@SerializedName("ResponseStatus")
	protected Boolean responseStatus;

	@SerializedName("ResponseCode")
	protected String responseCode;

	@SerializedName("ErrorMessages")
	protected List<String> errorMsg;
	
	public List<String> getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(List<String> errorMsg) {
		this.errorMsg = errorMsg;
	}
	public Boolean getResponseStatus() {
		return responseStatus;
	}

	public void setResponeStatus(Boolean responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getErrorCode() {
		return this.responseCode;
	}

}
