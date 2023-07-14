package com.globecapital.api.ft.OMEX;

import org.json.JSONObject;
import org.json.me.JSONException;

import com.globecapital.api.ft.OMEX.generics.OmexRequest;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class AuthenticateRequest  extends OmexRequest{
	
	
	public AuthenticateRequest() throws AppConfigNoKeyFoundException {
		super();
		
	}
	
	public void setApiKey() throws JSONException, AppConfigNoKeyFoundException {
		addToAuthReq(FTConstants.API_KEY, AppConfig.getValue("API_KEY"));
	}
	
	public void setSecretKey() throws JSONException, AppConfigNoKeyFoundException {
		addToAuthReq(FTConstants.SECRET_KEY, AppConfig.getValue("SECRET_KEY"));
	}
	
	public void setRequestId(String requestId) throws JSONException, AppConfigNoKeyFoundException {
		addToAuthReq(FTConstants.REQUEST_ID, requestId);
	}
	
//	private void addToAuthReq(String key, String value) throws JSONException{
//		this.AuthReq.put(key, value);
//	}
}
