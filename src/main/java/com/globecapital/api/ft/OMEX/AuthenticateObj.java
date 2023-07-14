package com.globecapital.api.ft.OMEX;

import org.json.me.JSONException;
import org.json.me.JSONObject;

public class AuthenticateObj extends JSONObject {

	public AuthenticateObj(JSONObject obj) throws JSONException {
		// TODO Auto-generated constructor stub
		super(obj.toString());
	}
	
	protected String requestId;
	protected String responseId;
	protected int statusCode;
	protected String errorString;
	protected String token;
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
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getErrorString() {
		return errorString;
	}
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
	
	
	
}
