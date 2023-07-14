package com.globecapital.api.ft.order;

import com.google.gson.annotations.SerializedName;

public class SendOrdReqObject {

	@SerializedName("Status")
	protected String sStatus;
	
	public void setStatus(String sStatus)
	{
		this.sStatus = sStatus;
	}
	
	public String getStatus()
	{
		return sStatus;
	}

}
