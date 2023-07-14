package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class EmailResponse extends GCApiResponse {

	@SerializedName("Msg")
	protected String message;

	@SerializedName("Status")
	protected String status;

	@SerializedName("mailstatus")
	protected String mailStatus;

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

	public String getMailStatus() {
		return mailStatus;
	}

	public void setMailStatus(String mailStatus) {
		this.mailStatus = mailStatus;
	}

}
