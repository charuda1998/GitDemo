package com.globecapital.api.gc.generics;

import java.util.HashMap;
import com.globecapital.api.generics.Api;
import com.globecapital.api.razorpay.generics.RazorPayResponse;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GCAPI_v1<Req extends GCApiRequest_v1, Resp extends RazorPayResponse> extends Api<Req, Resp> {

	//Below bean initialized with value in ApplicationContextListener.
	public static String API_BEAN = "";
	public GCAPI_v1(String serviceUrl) throws GCException {
		super(serviceUrl, API_BEAN);
		setConnectionHeaders();
	}

	protected void setConnectionHeaders() throws AppConfigNoKeyFoundException {
		HashMap<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		map.put("Access-Control-Expose-Headers", "Set-Cookie, Cookie, Content-Type");
		map.put("Access-Control-Allow-Methods", "POST");
		map.put("Access-Control-Allow-Credentials", "true");
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
