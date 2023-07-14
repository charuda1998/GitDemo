package com.globecapital.api.ft.user;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class ForgotPasswordRequest extends FTRequest {
	
	public ForgotPasswordRequest() throws AppConfigNoKeyFoundException {
		super();
	}
	
	public void setMobileNo(String mobileNo) throws JSONException {
		addToData(FTConstants.MOBILE_NO, mobileNo);
	}
	
	public void setPan(String pan) throws JSONException {
		addToData(FTConstants.PAN, pan);
	}
	
	public void setSecretQAFlag(String secretQAFlag) throws JSONException {
		addToData(FTConstants.SECRET_QA_FLAG, secretQAFlag);
	}
	
	public void setSendMode(String sendMode) throws JSONException {
		addToData(FTConstants.SEND_MODE, sendMode);
	}

}
