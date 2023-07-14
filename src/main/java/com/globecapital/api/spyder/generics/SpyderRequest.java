package com.globecapital.api.spyder.generics;

import java.util.HashMap;
import java.util.Map;

import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.api.generics.ApiRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class SpyderRequest extends ApiRequest{
	
	Map<String, String> params;
	
	public SpyderRequest() throws AppConfigNoKeyFoundException{
		super();
		this.respType = RESP_TYPE.JSON;
		this.params = new HashMap<String, String>();
		this.apiVendorName = SpyderConstants.VENDOR_NAME;
	}
	
	public void addParam(String key, String val) {
		this.params.put(key, val);
	}

	@Override
	public String toString() {
        
		String request = "";
		
		boolean firstParamAppended = false;
		
		for (Map.Entry<String, String> entry : this.params.entrySet()) {
			
			if(!firstParamAppended) {
	
				firstParamAppended = true;
			} else {
				request += "&";
			}
			request += entry.getKey();
			request += "=";
			request += entry.getValue();
		}
		
		return request;

	}

}
