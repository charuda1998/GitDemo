package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetFundHistoryRequest extends GCApiRequest {

	public GetFundHistoryRequest() throws AppConfigNoKeyFoundException {

		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}
	
	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}
	
	public void setTrCode(String trCode) {
	 	addToReq(GCConstants.INDEX_FUND_HISTORY_TR_CODE, trCode);
	}
	
	public void setFromDate(String fromDate) {
		addToReq(GCConstants.INDEX_FUND_HISTORY_FROM_DATE, fromDate);
	}

	public void setToDate(String toDate) {
		addToReq(GCConstants.INDEX_FUND_HISTORY_TO_DATE, toDate);
	}
	
	public void setBlank() {
		addToReq(GCConstants.INDEX_FUND_HISTORY_FUTURE_USE,"");
	}
	
}