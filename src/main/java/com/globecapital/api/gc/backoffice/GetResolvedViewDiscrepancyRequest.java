package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetResolvedViewDiscrepancyRequest extends GCApiRequest {

	public GetResolvedViewDiscrepancyRequest() throws AppConfigNoKeyFoundException {
		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_HOLDINGS_CLIENTCODE, clientCode);
	// }
	
	public void setScripCode(String sScripCode) {
		addToReq(GCConstants.INDEX_RESOLVED_VIEW_DISCREPANCY_SHCODE, sScripCode);
	}

}