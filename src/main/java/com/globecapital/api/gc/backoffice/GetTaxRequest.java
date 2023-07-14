package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetTaxRequest extends GCApiRequest {
	public GetTaxRequest() throws AppConfigNoKeyFoundException {

		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_TAX_CLIENTCODE, clientCode);
	// }

	public void setYear(String year) {
		addToReq(GCConstants.INDEX_TAX_YEAR, year);
	}

}