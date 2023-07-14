package com.globecapital.api.ft.user;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetMarketStatusRequest extends FTRequest {
	
	public GetMarketStatusRequest() throws AppConfigNoKeyFoundException, JSONException {
		super();
		setMsgCode("110");
	}
	
	public void setMsgCode(String sMsgCode) throws JSONException {
		addToData(FTConstants.MSG_CODE, sMsgCode);
	}
	
}
