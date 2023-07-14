package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetBankWithdrawalAmountResponse extends GCApiResponse {

	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;

	@SerializedName("REFNO")
	protected String referenceNo;

	@SerializedName("Availableamt")
	protected String availableAmt;

	@SerializedName("Withdrawableamt")
	protected String withdrawableAmt;
	
	@SerializedName("PENAMT")
	protected String pendingAmt;
	
	@SerializedName("PENREFID")
	protected String pendingRefId;
	
	@SerializedName("PENSTATUS")
	protected String pendingStatus;

	@SerializedName("DISCLAMER")
	protected String disclaimer;
	
	@SerializedName("PENDISCLAIMER")
	protected String pendingDisclaimer;

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	public String getAvailableAmt() {
		return availableAmt;
	}

	public void setAvailableAmt(String availableAmt) {
		this.availableAmt = availableAmt;
	}

	public String getWithdrawableAmt() {
		return withdrawableAmt;
	}

	public void setWithdrawableAmt(String withdrawableAmt) {
		this.withdrawableAmt = withdrawableAmt;
	}

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

	public String getPendingAmt() {
		return pendingAmt;
	}

	public String getPendingRefId() {
		return pendingRefId;
	}

	public String getPendingStatus() {
		return pendingStatus;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public String getPendingDisclaimer() {
		return pendingDisclaimer;
	}

	public void setPendingDisclaimer(String pendingDisclaimer) {
		this.pendingDisclaimer = pendingDisclaimer;
	}

}