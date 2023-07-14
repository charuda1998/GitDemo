package com.globecapital.api.ft.OMEX.generics;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.json.JSONException;

import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.jmx.Monitor;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidAPIResponseException;
import com.globecapital.services.exception.InvalidSession;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.msf.connections.http.HTTPConnection;

public class OmexApi<Req extends OmexRequest, Resp extends OmexResponse> {
	
	protected String serviceUrl;
	private String baseUrl;
	private Integer APIConnTimeout;
	private Integer APIReadTimeout;
	private String MONITOR_API_BEAN;
	
	protected HashMap<String, String> headers = new HashMap<String, String>();
	//public static String API_BEAN = "";
	public OmexApi(String serviceUrl) throws GCException {
		this.MONITOR_API_BEAN ="";
		setServiceUrl(serviceUrl);
		this.baseUrl = serviceUrl;
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

	public void setHeaders(HashMap<String, String> _m) {
		this.headers = _m;
	}

	
	public String post(Req req) throws GCException, JSONException, Exception {

		String postBody = (req == null) ? "" : req.toString();
		try {

			this.APIConnTimeout = req.getAPIConnectionTimeout();
			this.APIReadTimeout = req.getAPIReadTimeout();
			return post(postBody);
		} catch (GCException e) {
//			log.error(e);
			e.printStackTrace();
			throw e;
		}
	}
	
	public Resp post(Req request, Class<?> reponseClass, String appIDForLogging, String methodName) throws GCException, JSONException {

		String postBody = (request == null) ? "" : request.toString();
		try {

			this.APIConnTimeout = request.getAPIConnectionTimeout();
			this.APIReadTimeout = request.getAPIReadTimeout();
//			log.info("AppID: " + appIDForLogging +" Method : "+methodName+" API Request " + request.getAPIVendorName() + ": " + this.serviceUrl + " postparams: " + postBody);
			long requestTime = System.currentTimeMillis();			
			String response = post(postBody);
			long responseTime = System.currentTimeMillis();
			long reqTimeTaken = responseTime - requestTime;
//			log.info("AppID: " + appIDForLogging + " time_taken=" + reqTimeTaken +" Method : "+methodName+ " API Response " + request.getAPIVendorName() + ": " + response);
			return parseRawResponse(response, request, reponseClass);

		} catch (com.google.gson.JsonSyntaxException e) {
//			log.debug(e);
			throw new InvalidAPIResponseException("Request Failed.");
		}
	}
	
	protected Resp parseRawResponse(String response, Req request, Class<?> reponseClass)
			throws GCException, JSONException {
//		if (request.getRespType() == RESP_TYPE.JSON) {
			return createJSONRespObj(reponseClass, response);
//		}
//		throw new GCException(InfoIDConstants.DYNAMIC_MSG, "Invalid API Response,  Unknown reponse type");
	}
	
	@SuppressWarnings("unchecked")
	protected Resp createJSONRespObj(Class<?> reponseClass, String rawResponse) throws GCException {
		Gson gson = new Gson();
		JsonObject body = gson.fromJson(rawResponse, JsonObject.class);

		Boolean el = body.get("ResponseStatus").getAsBoolean();
		if (el)
			return (Resp) gson.fromJson(body, reponseClass);
		else
		{
			int code = body.get("ResponseCode").getAsInt();
			JsonArray errorArr = body.get("ErrorMessages").getAsJsonArray();
			
			if(code == 112)
				throw new InvalidSession (errorArr.getAsString());
			else if(errorArr.size() > 0)
				throw new GCException (InfoIDConstants.DYNAMIC_MSG, errorArr.getAsString());
			else if(body.get("ResponseObject").getAsJsonObject().get("Status").getAsString().equalsIgnoreCase(InfoMessage.getInfoMSG("info_msg.order_error")))
				throw new InvalidSession ();
			else
				throw new GCException (InfoIDConstants.DYNAMIC_MSG, 
						body.get("ResponseObject").getAsJsonObject().get("Status").getAsString());
			
		}
	}

	
	private String post(String postBody) throws GCException {
		String response = "";
		HTTPConnection httpConnection = null;
		try {

			httpConnection = new HTTPConnection(this.getCompleteUrl());
			httpConnection.setConnectionTimeout(this.APIConnTimeout);
			httpConnection.setReadTimeout(APIReadTimeout);
			httpConnection.disableLog();
			if (this.headers.size() > 1)
				httpConnection.setHeaders(headers);
			response = httpConnection.post(postBody);
		} catch (URISyntaxException e) {
			//log.error(e);
			Monitor.markFailure(MONITOR_API_BEAN, "Could not make API call");
			throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");
		} catch (SocketTimeoutException e) {
			//log.error("API Timed out : " + e);
			if (httpConnection != null) {
				int httpStatusCode = httpConnection.getReturnCode();
				String errorMsg = "API Timed out : " + httpStatusCode;
				Monitor.markFailure(MONITOR_API_BEAN, errorMsg);
			}
			//Commented the below line to capture the API timetaken
			//throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");
		} catch (IOException e) {
			//log.error(e);
			if (httpConnection != null) {
				int httpStatusCode = httpConnection.getReturnCode();
				String errorMsg = "Unable to connect API, Error Code:" + httpStatusCode;
				Monitor.markFailure(MONITOR_API_BEAN, errorMsg);
			}
			throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");

		}
		return response;
	}
	
	public String getCompleteUrl() {
		return this.serviceUrl;
	}


	protected void setServiceUrl(String serviceUrl) {
		String sReplace = "";
		if (serviceUrl.contains("|") && serviceUrl.contains(" ")) {
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
