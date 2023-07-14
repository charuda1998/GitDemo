package com.globecapital.api.gc.generics;

import java.util.HashMap;

import com.globecapital.api.generics.Api;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class GCApi<Req extends GCApiRequest, Resp extends GCApiResponse> extends Api<Req, Resp> {

	public static String API_BEAN = "";
	public GCApi() {
		super();
		setConnectionHeaders();
	}

	public GCApi(String serviceUrl) throws GCException {
		super(serviceUrl, GCApi.API_BEAN);
		setConnectionHeaders();
	}

	protected void setConnectionHeaders() {
		HashMap<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		map.put("Access-Control-Expose-Headers", "Set-Cookie, Cookie, Content-Type");
		map.put("Access-Control-Allow-Methods", "POST");
		map.put("Access-Control-Allow-Credentials", "true");
		setHeaders(map);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Resp createJSONRespObj(Class<?> reponseClass, String rawResponse) throws InvalidSession, GCException {
		Gson gs = new Gson();
		JsonObject body = gs.fromJson(rawResponse, JsonObject.class);
		Resp response = (Resp) gs.fromJson(rawResponse, reponseClass);
		String message = body.get("Msg").getAsString();
		if(message.contains("Session Expired")) {
			GCAPIAuthToken.resetAuthCode();
		}
		return response;
	}
	
	@Override
	protected void setServiceUrl(String serviceUrl) {
		String sReplace = "";
		if (serviceUrl.contains("|") && serviceUrl.contains(" ") && serviceUrl.contains("&")) {
			sReplace = serviceUrl.replace("|", "%7C");
			sReplace = sReplace.replace(" ", "%20");
			sReplace = sReplace.replace("&", "%26");
			this.serviceUrl = sReplace;
		} else if (serviceUrl.contains("|") && serviceUrl.contains(" ")) {
			sReplace = serviceUrl.replace("|", "%7C");
			sReplace = sReplace.replace(" ", "%20");
			this.serviceUrl = sReplace;
		} else if (serviceUrl.contains("|")) {
			sReplace = serviceUrl.replace("|", "%7C");
			this.serviceUrl = sReplace;
		} else if (serviceUrl.contains(" ")) {
			sReplace = serviceUrl.replace(" ", "%20");
			this.serviceUrl = sReplace;
		} else {
			this.serviceUrl = serviceUrl;
		}
	}
}
