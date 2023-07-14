package com.globecapital.api.ls.generics;

import java.util.HashMap;

import com.globecapital.api.generics.Api;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidSession;
import com.google.gson.Gson;

public class LSApi<Req extends LSRequest, Resp extends LSResponse> extends Api<Req, Resp> {

	public LSApi(String serviceUrl) {
		super(serviceUrl);
		setConnectionHeaders();
	}

	protected void setConnectionHeaders() {
		HashMap<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		map.put("Access-Control-Allow-Methods", "GET");
		map.put("Accept-Encoding", "gzip, deflate, br");
		map.put("Accept-Language", "en-US,en;q=0.5");
		map.put("Cache-Control", "max-age=0");
		map.put("Connection", "keep-alive");
		map.put("Host", "api.livesquawk.online");
		map.put("Upgrade-Insecure-Requests", "1");

		setHeaders(map);
	}

	@Override
	protected Resp createJSONRespObj(Class<?> reponseClass, String rawResponse) throws InvalidSession, GCException {
		Gson gs = new Gson();
		return (Resp) gs.fromJson(rawResponse, reponseClass);
	}

}
