package com.globecapital.services.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.config.InfoMessage;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.session.SessionHelper;
import com.msf.log.Logger;
import com.msf.sbu2.service.auth.AuthManager;
import com.msf.sbu2.service.auth.AuthPayload;
import com.msf.sbu2.service.common.SBU2RequestFields;
import com.msf.sbu2.service.exception.SBU2Exception;
import com.msf.sbu2.service.utils.JWT;

public class GCResponse extends JSONObject {

	protected JSONObject respObj;
	protected JSONObject dataObj;

	private String infoID;;
	private String infoMsg;
	private String appID;

	private String svcGroup;
	private String svcName;
	private String svcVersion;

	public static Logger log = Logger.getLogger(GCResponse.class);

	public GCResponse() throws JSONException {
		init();
	}

	public GCResponse(GCRequest gcRequest) throws JSONException {
		init();

		if (gcRequest != null)
			this.respObj.put(AppConstants.MSG_ID, gcRequest.getMsgID());
		setServerTime(System.currentTimeMillis() + "");

	}

	private void init() throws JSONException {
		this.respObj = new JSONObject();
		this.dataObj = new JSONObject();
		this.infoID = "0";
		this.infoMsg = "";
		this.svcGroup = "";
		this.svcName = "";
		this.appID = "";
		this.put(AppConstants.RESPONSE, this.respObj);

	}

	public void addToData(String key, Object value) throws JSONException {
		this.dataObj.put(key, value);
	}

	public void setData(JSONObject value) throws JSONException {
		this.dataObj = value;
	}

	public void setSuccessMsg(String msg) throws JSONException {
		this.dataObj.put(DeviceConstants.MSG, msg);
	}
	
	public String getMsgID() {
		try {
			return this.respObj.getString(AppConstants.MSG_ID);
		} catch (JSONException e) {
		}
		return null;
	}

	public String getInfoID() {
		return infoID;
	}

	public String getInfoMsg() {
		return infoMsg;
	}

	public String getAppID() {
		return appID;
	}

	public void setInfoID(String infoID) {
		this.infoID = infoID;
	}

	public void setInfoMsg(String infoMsg) {
		this.infoMsg = infoMsg;

	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public void setServerTime(String serverTime) {
		try {
			this.respObj.put(AppConstants.SERVER_TIME, serverTime);
		} catch (JSONException e) {
		}
	}

	public void clearData() {
		if (this.dataObj.length() > 0)
			this.dataObj = new JSONObject();
	}

	@Override
	public String toString() {
		try {
			this.respObj.put(AppConstants.INFO_ID, infoID);
			this.respObj.put(AppConstants.INFO_MSG, infoMsg);
			this.respObj.put(AppConstants.DATA, dataObj);
		} catch (JSONException e) {
		}

		return super.toString();
	}

	private HttpServletResponse httpResponse;

	private long reqTime = 0;

	public HttpServletResponse getHttpResponse() {
		return httpResponse;
	}

	public void setHttpResponse(HttpServletResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public void setRequestReceiveTime(long reqTime) {
		this.reqTime = reqTime;
	}

	public long getRequestReceivedTime() {
		return this.reqTime;
	}

	public void setSuccess() {
		setInfoID(InfoIDConstants.SUCCESS);
	}	

	public boolean isSuccess() {
		return getInfoID().equals(InfoIDConstants.SUCCESS);
	}

	public boolean isFailed()

	{
		return !isSuccess();
	}

	public JSONObject getData() {
		return dataObj;
	}

	public void setNoDataAvailable() {
		setInfoID(InfoIDConstants.NO_DATA);
		setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.no_data"));
	}

	public void setSession(String sessionID, String path, AuthPayload authPayload) throws SQLException {
		Cookie sessionCookie = new Cookie(AppConstants.SESSIONID, sessionID);
		sessionCookie.setPath(path + "/");
		//Fix applied as iOS not sending cookie value after killing the app(session should expire after 8 hours)
		sessionCookie.setMaxAge(SessionHelper.getSessionExpiry()); 
		httpResponse.addCookie(sessionCookie);
		
		String formatCookieContent;
		try {
			formatCookieContent = String.format("%s",AuthManager.issueAuthToken(authPayload));
			Cookie authCookie = new Cookie(SBU2RequestFields.AUTH_TOKEN, URLEncoder.encode( formatCookieContent,"UTF-8" ));
			authCookie.setPath("/");
			authCookie.setSecure(true);
			authCookie.setHttpOnly(true);
			authCookie.setMaxAge(SessionHelper.getSessionExpiry()); 
			httpResponse.addCookie(authCookie);
		} catch (SBU2Exception | UnsupportedEncodingException e) {
			log.info(e);
		}	
	
	}

	public String getSvcGroup() {
		return svcGroup;
	}

	public void setSvcGroup(String svcGroup) {
		this.svcGroup = svcGroup;
	}

	public String getSvcName() {
		return svcName;
	}

	public void setSvcName(String svcName) {
		this.svcName = svcName;
	}

}