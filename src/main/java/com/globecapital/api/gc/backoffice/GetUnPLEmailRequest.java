package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetUnPLEmailRequest extends GCApiRequest {

	public GetUnPLEmailRequest() throws AppConfigNoKeyFoundException {

		super();
		this.reqType = REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_UNREALISED_CLIENTCODE, clientCode);
	// }

	public void setDate(String date) {
		addToReq(GCConstants.INDEX_UNREALISED_DATE, date);
	}

	public void setYear(String year) {
		addToReq(GCConstants.INDEX_UNREALISED_YEAR, year);
	}

}