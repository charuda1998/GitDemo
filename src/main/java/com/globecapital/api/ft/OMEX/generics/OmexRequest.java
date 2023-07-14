package com.globecapital.api.ft.OMEX.generics;

import org.json.JSONObject;
import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.generics.ApiRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class OmexRequest extends JSONObject{
	
	protected JSONObject AuthReq;
	public Integer connTimeout;
	public Integer readTimeout;
	
	public OmexRequest() throws AppConfigNoKeyFoundException {
		this.AuthReq=new JSONObject();
		this.connTimeout = AppConfig.getIntValue("http.connection_timeout");
		this.readTimeout = AppConfig.getIntValue("http.read_timeout");
		// TODO Auto-generated constructor stub
	}
	
	public void addToAuthReq(String key, String value) throws JSONException{
		this.AuthReq.put(key, value);
	}
	
	public Integer getAPIConnectionTimeout() {
		return connTimeout;
	}
	
	public Integer getAPIReadTimeout() {
		return readTimeout;
	}
	
	
	@Override
	public String toString() {
		return this.AuthReq.toString();
	}
}
