package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetGCLoginRequest extends GCApiRequest {

	public GetGCLoginRequest() throws AppConfigNoKeyFoundException {
		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setSid(String sid) {
		addToReq(GCConstants.INDEX_SESSIONID, sid);
	}

	public void setUserName(String userName) {
		addToReq(GCConstants.INDEX_LOGIN_USERNAME, userName);
	}

	public void setPassword(String password) {
		addToReq(GCConstants.INDEX_LOGIN_PASSWORD, password);
	}

}