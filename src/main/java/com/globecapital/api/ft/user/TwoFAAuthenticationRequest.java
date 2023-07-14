package com.globecapital.api.ft.user;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class TwoFAAuthenticationRequest extends FTRequest {

	public TwoFAAuthenticationRequest() throws AppConfigNoKeyFoundException {
		super();
	}

	public void setToken(String tokenString) throws JSONException {
		addToData(FTConstants.TOKEN, tokenString);
	}

	public void setLoginAuth(Boolean loginauth) throws JSONException {
		addToData(FTConstants.isLoginAuth, loginauth);
	}

	public void setSsoLoginSession(Boolean ssoLoginSess) throws JSONException {
		addToData(FTConstants.ssoLoginSession, ssoLoginSess);
	}

}
