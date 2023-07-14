package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetCancelWithdrawalResponse extends GCApiResponse {

	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;

	@SerializedName("REFNO")
	protected String referenceNo;

	@SerializedName("Trxnstatus")
	protected String txnStatus;

	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
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
	
	public String getTxnStatus() {
		return txnStatus;
	}
	
	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}
}
