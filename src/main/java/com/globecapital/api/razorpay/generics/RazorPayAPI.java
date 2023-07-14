package com.globecapital.api.razorpay.generics;

import java.util.HashMap;
import com.globecapital.api.generics.Api;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RazorPayAPI<Req extends RazorPayRequest, Resp extends RazorPayResponse> extends Api<Req, Resp> {

	//Below bean initialized with value in ApplicationContextListener.
	public static String API_BEAN = "";
	public RazorPayAPI(String serviceUrl) throws GCException {
		super(serviceUrl, API_BEAN);
		setConnectionHeaders();
	}

	protected void setConnectionHeaders() throws AppConfigNoKeyFoundException {
		HashMap<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		map.put("Access-Control-Expose-Headers", "Set-Cookie, Cookie, Content-Type");
		map.put("Access-Control-Allow-Methods", "POST");
		map.put("Access-Control-Allow-Credentials", "true");
//		map.put(AppConfig.getValue("razorpay.key_id"), AppConfig.getValue("razorpay.key_secret"));
		String authorizationHeader = AppConfig.getValue("razorpay.key_id")+":"+AppConfig.getValue("razorpay.key_secret"); 
		map.put("Authorization","Basic "+ java.util.Base64.getEncoder().encodeToString(authorizationHeader.getBytes()));
		setHeaders(map);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Resp createJSONRespObj(Class<?> reponseClass, String rawResponse) throws GCException {
		Gson gson = new Gson();
		JsonObject body = gson.fromJson(rawResponse, JsonObject.class);

		return (Resp) gson.fromJson(body, reponseClass);
	}
}
