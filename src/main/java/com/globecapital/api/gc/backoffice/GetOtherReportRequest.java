package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetOtherReportRequest extends GCApiRequest {
	public GetOtherReportRequest() throws AppConfigNoKeyFoundException {

		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_OTHER_CLIENTCODE, clientCode);
	// }

	public void setFromDate(String fromDate) {
		addToReq(GCConstants.INDEX_OTHER_FROM_DATE, fromDate);
	}

	public void setToDate(String toDate) {
		addToReq(GCConstants.INDEX_OTHER_TO_DATE, toDate);
	}

	public void setSegment(String segment) {
		addToReq(GCConstants.INDEX_OTHER_SEGMENT, segment);
	}

	public void setYear(String year) {
		addToReq(GCConstants.INDEX_OTHER_YEAR, year);
	}

}