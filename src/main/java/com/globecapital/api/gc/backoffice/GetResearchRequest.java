package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetResearchRequest extends GCApiRequest {

	public GetResearchRequest() throws AppConfigNoKeyFoundException {

		super();
		this.reqType=REQ_TYPE.KEY_VALUE;
		this.respType = RESP_TYPE.JSONArray;
	}

	public void setSegment(String segment) {
		addToReq(GCConstants.SEGMENT, segment);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.CODE, clientCode);
	// }
}