package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetRLEmailDownloadRequest extends GCApiRequest {

	public GetRLEmailDownloadRequest() throws AppConfigNoKeyFoundException {

		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_REALISED_CLIENTCODE, clientCode);
	// }

	public void setFromDate(String fromDate) {
		addToReq(GCConstants.INDEX_REALISED_FROM_DATE, fromDate);
	}

	public void setToDate(String toDate) {
		addToReq(GCConstants.INDEX_REALISED_TO_DATE, toDate);
	}

	public void setType(String type) {
		addToReq(GCConstants.INDEX_REALISED_TYPE, type);
	}

	public void setYear(String year) {
		addToReq(GCConstants.INDEX_REALISED_YEAR, year);
	}

}