package com.globecapital.api.ft.generics;

import org.json.JSONException;

import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.api.generics.ApiRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;

public class FTRequest extends ApiRequest {

	public FTRequest() throws AppConfigNoKeyFoundException {
		super();
		setProductCode(FTConstants.PRODUCT_CODE_MOBILE);
		this.respType = RESP_TYPE.JSON;
		this.apiVendorName=FTConstants.VENDOR_NAME;
	}
	
	public void setJSession(String jSession) throws JSONException, GCException {
		addToReq(FTConstants.J_SESSION, jSession);
	}

	public void setJKey(String jkey) throws JSONException {
		addToReq(FTConstants.J_KEY, jkey);
	}

	public void setProductCode(String productCode) throws JSONException {
		addToReq(FTConstants.PROD_CODE, productCode);
	}
	
	public void setGroupId(String groupid) throws JSONException {
		addToData(FTConstants.GROUP_ID, groupid);
	}

	public void setUserID(String userName) throws JSONException {
		addToData(FTConstants.USER_ID, userName);
	}
	
	public void setClientOrdNo(String clientOrdNo) throws JSONException {
		addToData(FTConstants.CLIENT_ORD_NO, clientOrdNo);
	}

}
