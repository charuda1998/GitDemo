package com.globecapital.api.spyder.generics;

import com.globecapital.api.generics.Api;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidSession;
import com.google.gson.Gson;

public class SpyderApi<Req extends SpyderRequest, Resp extends SpyderResponse> extends Api<Req, Resp> {

	//Below bean initialized with value in ApplicationContextListener.
	public static String API_BEAN="";
	public SpyderApi(String serviceUrl) throws GCException {
		super(serviceUrl, API_BEAN);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Resp createJSONRespObj(Class<?> reponseClass, String rawResponse) throws InvalidSession, GCException {
		Gson gs = new Gson();
		return (Resp) gs.fromJson(rawResponse, reponseClass);
	}

}
