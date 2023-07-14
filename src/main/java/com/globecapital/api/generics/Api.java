package com.globecapital.api.generics;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.json.JSONException;

import com.globecapital.business.report.GCHttpConnection;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.jmx.Monitor;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidAPIResponseException;
import com.globecapital.services.exception.InvalidSession;
import com.msf.connections.http.HTTPConnection;
import com.msf.log.Logger;

public abstract class Api<Req extends ApiRequest, Resp extends ApiResponse> {

	protected static Logger log = Logger.getLogger(Api.class);
	protected String serviceUrl;
	private String baseUrl;
	private Integer APIConnTimeout;
	private Integer APIReadTimeout;
	private String MONITOR_API_BEAN;
	
	abstract protected Resp createJSONRespObj(Class<?> reponseClass, String rawResponse)
			throws InvalidSession, GCException;

	protected HashMap<String, String> headers = new HashMap<String, String>();

	public enum RESP_TYPE {
		JSON, XML, PLAIN, JSONArray
	}

	public void setHeaders(HashMap<String, String> _m) {
		this.headers = _m;
	}

	public Api(String serviceUrl, String _apiBean) throws GCException {
		this.MONITOR_API_BEAN = _apiBean;
		setServiceUrl(serviceUrl);
		this.baseUrl = serviceUrl;
	}

	public Api(String serviceUrl) {
		this.MONITOR_API_BEAN = "";
		setServiceUrl(serviceUrl);
		this.baseUrl = serviceUrl;
	}

	public Api() {

		this.MONITOR_API_BEAN = "";
	}

	protected void setMonitoringID(String _monitorID) {
		this.MONITOR_API_BEAN = _monitorID;
	}

	public Resp post(Req request, Class<?> reponseClass, String appIDForLogging, String methodName) throws GCException, JSONException {

		String postBody = (request == null) ? "" : request.toString();
		try {

			this.APIConnTimeout = request.getAPIConnectionTimeout();
			this.APIReadTimeout = request.getAPIReadTimeout();
			log.info("AppID: " + appIDForLogging +" Method : "+methodName+" API Request " + request.getAPIVendorName() + ": " + this.serviceUrl + " postparams: " + postBody);
			long requestTime = System.currentTimeMillis();			
			String response = post(postBody);
			long responseTime = System.currentTimeMillis();
			long reqTimeTaken = responseTime - requestTime;
			log.info("AppID: " + appIDForLogging + " time_taken=" + reqTimeTaken +" Method : "+methodName+ " API Response " + request.getAPIVendorName() + ": " + response);
			return parseRawResponse(response, request, reponseClass);

		} catch (com.google.gson.JsonSyntaxException e) {
			log.debug(e);
			throw new InvalidAPIResponseException("Request Failed.");
		}
	}

	/* For fetching response containing large-sized JSON objects */

	public String post(Req req) throws GCException, JSONException, Exception {

		String postBody = (req == null) ? "" : req.toString();
		try {

			this.APIConnTimeout = req.getAPIConnectionTimeout();
			this.APIReadTimeout = req.getAPIReadTimeout();
			return post(postBody);
		} catch (GCException e) {
			log.error(e);
			e.printStackTrace();
			throw e;
		}
	}

	/* For fetching response containing JSONArray */

	public String get(Req request, String appIDForLogging, String methodName) throws GCException, JSONException, Exception {
		try {
			setServiceUrl(this.baseUrl + request.toString());
			this.APIConnTimeout = request.getAPIConnectionTimeout();
			this.APIReadTimeout = request.getAPIReadTimeout();
			log.info("AppID: " + appIDForLogging + " Method : " + methodName + " API Request " + request.getAPIVendorName() + ": " + getCompleteUrl());
			long requestTime = System.currentTimeMillis();
			String response = getRequest();
			long responseTime = System.currentTimeMillis();
			long reqTimeTaken = responseTime - requestTime;
			log.info("AppID: " + appIDForLogging + " time_taken=" + reqTimeTaken + " Method : " + methodName + " API Response " + request.getAPIVendorName() + ": " + response);
			return response;
		} catch (GCException e) {
			throw e;
		}
	}

	public Resp get(Req request, Class<?> reponseClass, String appIDForLogging, String methodName) throws GCException, JSONException {
		try {
			setServiceUrl(this.baseUrl + request.toString());
			this.APIConnTimeout = request.getAPIConnectionTimeout();
			this.APIReadTimeout = request.getAPIReadTimeout();
			log.info("AppID: " + appIDForLogging +" Method : "+methodName+" API Request " + request.getAPIVendorName() + ": " + getCompleteUrl());
			long requestTime = System.currentTimeMillis();
			String response = getRequest();
			long responseTime = System.currentTimeMillis();
			long reqTimeTaken = responseTime - requestTime;
			log.info("AppID: " + appIDForLogging + " time_taken=" + reqTimeTaken +" Method : "+methodName+" API Response " + request.getAPIVendorName() + ": " + response);
			
			return parseRawResponse(response, request, reponseClass);

		} catch (com.google.gson.JsonSyntaxException e) {
			log.error(e);
			throw new InvalidAPIResponseException("Request Failed.");
		}
	}

	public GCHttpConnection getDownloadResp(Req request, Class<?> reponseClass, String appIDForLogging)
			throws GCException, JSONException {

		try {

			setServiceUrl(this.baseUrl + request.toString());
			this.APIConnTimeout = request.getAPIConnectionTimeout();
			this.APIReadTimeout = request.getAPIReadTimeout();
			log.info("AppID: " + appIDForLogging + " API Request " + request.getAPIVendorName() + " : " + getCompleteUrl());
			return getDownloadHTTPConnection();

		} catch (GCException e) {
			throw e;

		}
	}

	public GCHttpConnection getDownloadHTTPConnection() throws GCException {

		GCHttpConnection httpConnection = null;

		try {
			httpConnection = new GCHttpConnection(getCompleteUrl());
			httpConnection.setConnectionTimeout(this.APIConnTimeout);
			httpConnection.setReadTimeout(this.APIReadTimeout);
			if (this.headers.size() > 1)
				httpConnection.setHeaders(headers);

			return httpConnection;
		} catch (URISyntaxException e) {
			log.error(e);
			Monitor.markFailure(MONITOR_API_BEAN, "Could not make API call");
			throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");
		}
	}

	protected Resp parseRawResponse(String response, Req request, Class<?> reponseClass)
			throws GCException, JSONException {
		if (request.getRespType() == RESP_TYPE.JSON) {
			return createJSONRespObj(reponseClass, response);
		}
		throw new GCException(InfoIDConstants.DYNAMIC_MSG, "Invalid API Response,  Unknown reponse type");
	}

	protected String getRequest() throws GCException {
		String response = "";
		HTTPConnection httpConnection = null;
		try {
			httpConnection = new HTTPConnection(getCompleteUrl());
			httpConnection.setConnectionTimeout(this.APIConnTimeout);
			httpConnection.setReadTimeout(APIReadTimeout);
			httpConnection.disableLog();
			if (this.headers.size() > 1)
				httpConnection.setHeaders(headers);

			response = httpConnection.get();

		} catch (URISyntaxException e) {
			log.error(e);
			Monitor.markFailure(MONITOR_API_BEAN, "Could not make API call");
			throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");
		} catch (SocketTimeoutException e) {
			log.error("API Timed out : " + e);
			if (httpConnection != null) {
				int httpStatusCode = httpConnection.getReturnCode();
				String errorMsg = "API Timed out : " + httpStatusCode;
				log.info(errorMsg);
				Monitor.markFailure(MONITOR_API_BEAN, errorMsg);
			}
			//Commented the below line to capture the API timetaken
			//throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");
		} catch (IOException e) {
			log.error(e);
			if (httpConnection != null) {
				int httpStatusCode = httpConnection.getReturnCode();
				String errorMsg = "Unable to connect API, Error Code:" + httpStatusCode;
				log.info(errorMsg);
				Monitor.markFailure(MONITOR_API_BEAN, errorMsg);
			}
			throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");

		}

		return response;

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
			log.error(e);
			Monitor.markFailure(MONITOR_API_BEAN, "Could not make API call");
			throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");
		} catch (SocketTimeoutException e) {
			log.error("API Timed out : " + e);
			if (httpConnection != null) {
				int httpStatusCode = httpConnection.getReturnCode();
				String errorMsg = "API Timed out : " + httpStatusCode;
				Monitor.markFailure(MONITOR_API_BEAN, errorMsg);
			}
			//Commented the below line to capture the API timetaken
			//throw new InvalidAPIResponseException("Could not make API call. Kindly try again later");
		} catch (IOException e) {
			log.error(e);
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
