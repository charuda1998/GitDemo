
package com.globecapital.api.gc.generics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.api.generics.ApiRequest;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GCApiRequest extends ApiRequest {

	Map<String, String> requestParamsKeyValue;
	TreeMap<Integer, String> requestParamsIndexValue;

	public GCApiRequest() throws AppConfigNoKeyFoundException {
		super();
		this.respType = RESP_TYPE.JSON;
		this.requestParamsKeyValue = new LinkedHashMap<String, String>();
		this.requestParamsIndexValue = new TreeMap<Integer, String>();
		this.apiVendorName = GCConstants.VENDOR_NAME;

	}

	protected REQ_TYPE reqType;

	public REQ_TYPE getReqType() {

		return reqType;
	}

	public void addToReq(String key, String val) {
		this.requestParamsKeyValue.put(key, val);

	}

	public void addToReq(Integer index, String val) {
		this.requestParamsIndexValue.put(index, val);

	}

	public void setClientCode(String clientCode) {
		//temp to remove extra space from username
		  clientCode=clientCode.toUpperCase().trim();
		//TODO: temp changes - no need to pick for live deploymnt - can be removed later
		//addToReq(GCConstants.INDEX_CONTRACT_EMAIL_CLIENTCODE, clientCode);
		addToReq(GCConstants.INDEX_CONTRACT_EMAIL_CLIENTCODE, AppConfig.optValue(clientCode, clientCode));
	}

	@Override
	public String toString() {
		String request = "";
		boolean isFirstValueAppended = false;

		if (reqType == REQ_TYPE.PIP_SEPARTED) {

			for (Map.Entry<Integer, String> entry : this.requestParamsIndexValue.entrySet()) {

				if (!isFirstValueAppended) {

					isFirstValueAppended = true;
				} else {
					request += "|";
				}

				request += entry.getValue();
			}
		} else {
			for (Map.Entry<String, String> entry : this.requestParamsKeyValue.entrySet()) {

				if (!isFirstValueAppended) {

					isFirstValueAppended = true;
				} else {
					request += "&";
				}
				request += entry.getKey();
				request += "=";
				request += entry.getValue();
			}
		}
		return request;
	}
}