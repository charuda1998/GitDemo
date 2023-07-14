package com.globecapital.api.ft.market;

import com.google.gson.annotations.SerializedName;

public class GetExchangeMessagesObjectRow{

	
	@SerializedName("Time")
	protected String time;

	@SerializedName("Msg")
	protected String msg;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
