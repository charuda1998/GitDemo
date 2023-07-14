package com.globecapital.api.gc.generics;

import org.json.JSONException;

import com.globecapital.api.gc.backoffice.GetGCLoginAPI;
import com.globecapital.api.gc.backoffice.GetGCLoginRequest;
import com.globecapital.api.gc.backoffice.GetGCLoginResponse;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.msf.log.Logger;

public class GCAPIAuthToken {

	private static String authCode;
	public static String message;
	private static Logger log = Logger.getLogger(GCAPIAuthToken.class);

	public static String getAuthToken() throws JSONException, GCException {

		log.debug("getting auth code");

		if (authCode == null) {

			try {

//				invokeGCLogin();
				authCode = loadAuthToken();
			} catch (Exception e) {
				log.error("Exception while fetching authcode" + e);

			}
		}
		return authCode;
	}

	public  static void resetAuthCode() {
		authCode= null;
	}
	
	private static String loadAuthToken() throws AppConfigNoKeyFoundException {
		return AppConfig.getValue("gc.api.AuthToken");
	}
	
	private static void invokeGCLogin() throws Exception {

		log.debug("invoking");
		
		GetGCLoginAPI loginApi = new GetGCLoginAPI();
		GetGCLoginRequest loginRequest = new GetGCLoginRequest();
		loginRequest.setSid(AppConfig.getValue("gc.api.sid"));
		loginRequest.setUserName(AppConfig.getValue("gc.api.userName"));
		loginRequest.setPassword(AppConfig.getValue("gc.api.password"));
		GetGCLoginResponse loginResponse = loginApi.get(loginRequest, GetGCLoginResponse.class, "","GetLogin");
		authCode = loginResponse.getAuthCode();
		message = loginResponse.getMsg();
		if (authCode == null || authCode.isEmpty())
			throw new Exception("Invalid auth code");
	}

}
