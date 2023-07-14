package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetBankTransactionRows {
	
	@SerializedName("ReleasedAmt")
	protected String releasedAmt;
	
	@SerializedName("ReqDatetime")
	protected String dateTime;
	
	@SerializedName("RequestAmt")
	protected String requestAmt;
	
	@SerializedName("Segments")
	protected String segment;
	
	@SerializedName("Status")
	protected String status;
	
	@SerializedName("Trxnid")
	protected String trxnId;
	
	public String getReleasedAmt() {
		return releasedAmt;
	}

	public void setReleasedAmt(String releasedAmt) {
		this.releasedAmt = releasedAmt;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getRequestAmt() {
		return requestAmt;
	}

	public void setRequestAmt(String requestAmt) {
		this.requestAmt = requestAmt;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTrxnId() {
		return trxnId;
	}

	public void setTrxnId(String trxnId) {
		this.trxnId = trxnId;
	}
	
}
