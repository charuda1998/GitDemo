package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetPayInTxnRows {
	
	@SerializedName("domainReferenceNo")
	protected String domainReferenceNo;
	
	@SerializedName("errorCode")
	protected String errorCode;
	
	@SerializedName("errorMessage")
	protected String errorMessage;

	public String getDomainReferenceNo() {
		return domainReferenceNo;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
}
