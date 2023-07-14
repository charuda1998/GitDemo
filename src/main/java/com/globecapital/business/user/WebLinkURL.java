package com.globecapital.business.user;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;

import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;

public class WebLinkURL {


	public static String getWeblinkUrl(Session session)
			throws AppConfigNoKeyFoundException, JSONException, GCException {

		String url = AppConfig.getValue("gc.api.getPledgeLink") + AppConfig.getValue("gc.api.WebLinkAuthToken") + "|KDC" + "|" + session.getUserID();

		return url;
	}

	public static String getPledgeLinkUrl(String sUserID) throws AppConfigNoKeyFoundException, JSONException, GCException {
		
		String url = AppConfig.getValue("gc.api.getPledgeLink") + AppConfig.getValue("gc.api.WebLinkAuthToken") + "|PLDG" + "|" + sUserID;
		
		return url;
	}

	public static String getIPOLinkUrl(String sUserID) throws AppConfigNoKeyFoundException, JSONException, GCException {
		String url = AppConfig.getValue("gc.api.getIPOLink") + AppConfig.getValue("myglobe.authorization.token") + "&code=" + encryptClientCodeForIPO(sUserID);
		
		return url;
	}
	
	private static String encryptClientCodeForIPO(String code) throws GCException {
		byte encrypt_code [] = new byte[code.length()];
		try {
			encrypt_code = code.getBytes("UTF-8");
			byte encoded [] = Base64.encodeBase64(encrypt_code);
			return new String(encoded);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new GCException("Not able to encrypt IPO token" + code);
		}
	}
}
