package com.globecapital.api.generics;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.msf.log.Logger;

public class ApiRequest extends JSONObject {
	public static Logger log = Logger.getLogger(ApiRequest.class);
	protected RESP_TYPE respType = RESP_TYPE.JSON;
	public Integer connTimeout;
	public Integer readTimeout;
	protected JSONObject reqObj;
	protected JSONObject dataObj;
	protected String apiVendorName;
	protected List<String> maskList = new ArrayList<>();
	public boolean isMaskEnabled;

	/* For Logging Purpose */
	protected JSONObject dataObjForLogging;
	protected JSONObject reqObjForLogging;

	public ApiRequest() throws AppConfigNoKeyFoundException {
		this.reqObj = new JSONObject();
		this.dataObj = new JSONObject();
		this.connTimeout = AppConfig.getIntValue("http.connection_timeout");
		this.readTimeout = AppConfig.getIntValue("http.read_timeout");
		this.apiVendorName = "";
	}
	
	public enum REQ_TYPE {
		PIP_SEPARTED, KEY_VALUE
	}

	public void addToReq(String key, Object val) throws JSONException {
		this.reqObj.put(key, val);
	}

	public void addToData(String key, Object val) throws JSONException {
		this.dataObj.put(key, val);
	}

	public RESP_TYPE getRespType() {
		return respType;
	}

	public Integer getAPIConnectionTimeout() {
		return connTimeout;
	}
	
	public Integer getAPIReadTimeout() {
		return readTimeout;
	}

	public String getAPIVendorName() {
		return this.apiVendorName;
	}

	public void addMaskToList(List<String> maskList) {
		this.maskList = maskList;
	}
	
	@Override
	public String toString() {
		this.reqObj.put(FTConstants.J_DATA, this.dataObj);
		this.put(FTConstants.PARAMETERS, this.reqObj);
		return super.toString();
	}
	
	public void maskValueInData(String key) throws JSONException {
		JSONObject dataObject = this.dataObjForLogging;
		dataObject.put(key, "****");
	}

	public String toStringForLogging() {
		this.reqObjForLogging = new JSONObject(this.reqObj,JSONObject.getNames(this.reqObj));
		this.dataObjForLogging = new JSONObject(this.dataObj,JSONObject.getNames(this.dataObj));
		for(String maskKey : this.maskList) 
			maskValueInData(maskKey);
		this.reqObjForLogging.put(FTConstants.J_DATA, this.dataObjForLogging);
		this.put(FTConstants.PARAMETERS, this.reqObjForLogging);
		return super.toString();
	}
}
