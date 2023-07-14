package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetSaudaEmailDownloadRequest extends GCApiRequest {

	public GetSaudaEmailDownloadRequest() throws AppConfigNoKeyFoundException {
		super();
		this.reqType = REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setAuthCode(String authCode) {
		addToReq(GCConstants.INDEX_SESSIONID, authCode);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_SAUDA_EMAIL_CLIENTCODE, clientCode);
	// }

	public void setYear(String year) {
		addToReq(GCConstants.INDEX_SAUDA_EMAIL_YEAR, year);
	}

	public void setDate(String fromDate) {
		addToReq(GCConstants.INDEX_SAUDA_EMAIL_DATE, fromDate);
	}

}