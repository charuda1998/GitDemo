package com.globecapital.api.ft.OMEX;


import org.json.me.JSONException;

import com.globecapital.api.ft.OMEX.generics.OmexRequest;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class RegisterRequest extends OmexRequest{
		
	public RegisterRequest() throws AppConfigNoKeyFoundException {
		super();
	}
	
	public void setToken(String token) throws JSONException, AppConfigNoKeyFoundException {
		addToAuthReq(FTConstants.JTOKEN, token);
	}
	
	public void setConnMode() throws JSONException, AppConfigNoKeyFoundException {
		addToAuthReq(FTConstants.CON_MODE, AppConfig.getValue("CONNECTION_MODE"));
	}
	
	public void setUrl() throws JSONException, AppConfigNoKeyFoundException {
		addToAuthReq(FTConstants.JURL, AppConfig.getValue("getomexmessage.api.url"));
	}
	
	public void setReqId(String requestId) throws JSONException, AppConfigNoKeyFoundException {
		addToAuthReq(FTConstants.REQUEST_ID,requestId);
	}

}
