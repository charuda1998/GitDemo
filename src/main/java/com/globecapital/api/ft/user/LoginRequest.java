package com.globecapital.api.ft.user;

import org.json.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class LoginRequest extends FTRequest {
	
	public LoginRequest() throws AppConfigNoKeyFoundException {
		super();
		
		addToData(FTConstants.MSG_CODE, FTConstants.MSG_CODE_VALUE);
		addToData(FTConstants.CONNECTION_TYPE, FTConstants.CONNECTION_TYPE_VALUE);
		addToData(FTConstants.LOGIN_TAG, FTConstants.LOGIN_TAG_MOBILE);
		addToData(FTConstants.PASSWORD_ENCRYPT, true);
		addToData(FTConstants.AUTHENTICATE_FLAG, 0);
		addToData(FTConstants.I_FRAME, false);
		addToData(FTConstants.GROUP_ID, "");
		addToData(FTConstants.SSO_TOKEN, "");
	}

	public void setPassword(String password) throws JSONException {
		addToData(FTConstants.PASSWORD, password);
	}

	public void setNewPassword(String newPassword) throws JSONException {
		addToData(FTConstants.NEW_PASSWORD, newPassword);
	}

	public void setSSOToken(String ssoToken) throws JSONException {
		addToData(FTConstants.SSO_TOKEN, ssoToken);
	}

	public void setForceLoginTag(boolean forceLoginTag) throws JSONException {
		
		if(forceLoginTag)
			addToData(FTConstants.FORCE_LOGIN_TAG, FTConstants.FORCE_LOGIN);
	}
	
	public void setIPAddr(String sIPAddr)
	{
		addToData(FTConstants.IP_ADDR, sIPAddr);
	}

}
