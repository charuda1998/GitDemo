package com.globecapital.api.ft.OMEX;

import org.json.me.JSONException;
import org.json.me.JSONObject;

public class RegisterObject extends JSONObject{
	
	public RegisterObject(JSONObject obj) throws JSONException {
		// TODO Auto-generated constructor stub
		super(obj.toString());
	}
	
	protected String requestId;
	protected String responseId;
	protected String statusCode;
	protected String errorString;
	protected int heartbeat_interval;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getResponseId() {
		return responseId;
	}
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getErrorString() {
		return errorString;
	}
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
	public int getHeartbeat_interval() {
		return heartbeat_interval;
	}
	public void setHeartbeat_interval(int heartbeat_interval) {
		this.heartbeat_interval = heartbeat_interval;
	}
	
}
