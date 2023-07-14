package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiResponse;
import com.google.gson.annotations.SerializedName;

public class GetGCLoginResponse extends GCApiResponse {

	@SerializedName("Sessionid")
	protected String Sessionid;

	@SerializedName("Msg")
	protected String Msg;

	@SerializedName("Status")
	protected String Status;

	public String getAuthCode() {
		return Sessionid;
	}

	public void setAuthCode(String authCode) {
		Sessionid = authCode;
	}

	public String getMsg() {
		return Msg;
	}

	public void setMsg(String msg) {
		Msg = msg;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

}