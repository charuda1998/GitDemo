package com.globecapital.services.common;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.audit.GCAuditObject;
import com.globecapital.constants.AppConstants;
import com.globecapital.services.exception.InvalidRequestKeyException;
import com.globecapital.services.exception.InvalidSession;
import com.globecapital.services.session.Session;
import com.msf.utils.helper.Helper;

public class GCRequest extends JSONObject {
		
	private JSONObject reqObj;
	private JSONObject dataObj;
	private String msgID;
	private String appId;
	//private JSONObject echoObj = null;

	// For masking data
	private String request;
	private JSONObject forLogging;
	private HttpServletRequest httpRequest;

	// private HttpSession httpSession;
	private Session session;
	
	private GCAuditObject auditObj;

	public GCRequest(String request) throws InvalidRequestKeyException, JSONException {
		super(request);

		this.request = request;
		this.reqObj = this.getJSONObject(AppConstants.REQUEST);

		setMessageID();
		this.reqObj.put(AppConstants.MSG_ID, this.msgID);

		try 
		{
			if (this.reqObj.has(AppConstants.APP_ID))
				this.appId = this.reqObj.getString(AppConstants.APP_ID);
		} 
		catch (JSONException e) 
		{
			throw new InvalidRequestKeyException();
		}
	}

	public JSONObject getData() throws JSONException {
		// Lazy Loading
		if (this.dataObj == null)
			this.dataObj = this.reqObj.getJSONObject(AppConstants.DATA);

		return this.dataObj;
	}

	public JSONObject getRequest() {
		return this.reqObj;
	}

	public String getMsgID() {
		return this.msgID;
	}

	public String getAppID() {
		return this.appId;
	}
	
	public GCAuditObject getAuditObj() {
		return auditObj;
	}

	public void setAuditObj(GCAuditObject auditObj) {
		this.auditObj = auditObj;
	}

	private void createLoggingObj() throws JSONException {
		if (this.forLogging == null) {
			this.forLogging = new JSONObject(this.request);
			this.forLogging.getJSONObject(AppConstants.REQUEST).put(AppConstants.MSG_ID, this.msgID);
		}
	}

	public void maskValueInData(String key) throws JSONException {
		createLoggingObj();

		JSONObject dataObject = this.forLogging.getJSONObject(AppConstants.REQUEST).getJSONObject(AppConstants.DATA);
		dataObject.put(key, "****");
	}

	public void maskValueInData(String[] keys) throws JSONException {
		if (keys == null)
			return;

		createLoggingObj();

		JSONObject dataObject = this.forLogging.getJSONObject(AppConstants.REQUEST).getJSONObject(AppConstants.DATA);
		for (String k : keys)
			dataObject.put(k, "****");
	}

	public void maskValueofArrayinData(String arrName, String key) throws JSONException {

		createLoggingObj();
		JSONArray dataArr = this.forLogging.getJSONObject(AppConstants.REQUEST).getJSONObject(AppConstants.DATA).getJSONArray(arrName);

		for (int i = 0; i < dataArr.length(); i++) {
			JSONObject replaceObject = dataArr.getJSONObject(i);
			replaceObject.put(key, "****");
		}

	}

	public void maskValueinObjectinData(String objName, String key) throws JSONException {
		createLoggingObj();
		JSONObject dataObj = this.forLogging.getJSONObject(AppConstants.REQUEST).getJSONObject(AppConstants.DATA).getJSONObject(objName);
		dataObj.put(key, "****");
	}

	public String toS() {
		return getObjForLogging().toString();
	}

	public JSONObject getObjForLogging() {
		if (this.forLogging != null)
			return this.forLogging;

		return this;
	}

	public String getClientIP() {
		String ipAddress = getHttpRequest().getHeader("X-Real-IP");
		if (ipAddress == null) {
			ipAddress = getHttpRequest().getRemoteAddr();
		}
		return ipAddress;
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public void setSession(Session s) {
		this.session = s;
	}

	public Session getSession() throws InvalidSession {

		if (this.session == null)
			throw new InvalidSession("Your session is not valid. Please login");

		return this.session;
	}

	public String createSession(boolean isActive) {

		String jSessionID = null;

		if (httpRequest != null) {

			Cookie[] cookies = httpRequest.getCookies();

			if (cookies != null) {

				for (Cookie c : cookies) {

					if (c.getName().equals(AppConstants.SESSIONID)) {
						jSessionID = c.getValue();
						break;
					}
				}
			}
		}

		if (isActive) {

			jSessionID = Helper.MD5sum(this.toS() + System.currentTimeMillis());
		}

		if (jSessionID == null || jSessionID.length() == 0)
			return null;

		return jSessionID;
	}

	public String getOptFromData(String key, String value) throws JSONException {

		return getData().optString(key, value);
	}

	public String getFromData(String key) throws InvalidRequestKeyException {

		try {
			return getData().getString(key);
		} catch (JSONException e) {
			throw new InvalidRequestKeyException(String.format("%s key not found in request", key));
		}
	}
	
	public JSONArray getArrayFromData(String key) throws InvalidRequestKeyException {

		try {
			return getData().getJSONArray(key);
		} catch (JSONException e) {
			throw new InvalidRequestKeyException(String.format("%s key not found in request", key));
		}
	}
	
	public JSONObject getObjectFromData(String key) throws InvalidRequestKeyException {

		try {
			return getData().getJSONObject(key);
		} catch (JSONException e) {
			throw new InvalidRequestKeyException(String.format("%s key not found in request", key));
		}
	}

	public JSONObject getOptObjectFromData(String key) throws InvalidRequestKeyException {

		return getData().optJSONObject(key);
	}
	
	private void setMessageID() {
		this.msgID = UUID.randomUUID() + "";
	}

}
