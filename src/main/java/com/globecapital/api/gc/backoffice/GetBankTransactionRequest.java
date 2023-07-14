package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetBankTransactionRequest extends GCApiRequest {

	public GetBankTransactionRequest() throws AppConfigNoKeyFoundException {

		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}
//	public void setAuthCode(String authCode) {
//		addToReq(GCConstants.TOKEN, authCode);
//	}
	
	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}
	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_BANK_CLIENTCODE, clientCode);
	// }
	
	public void setFromDate(String fromDate) {
		addToReq(GCConstants.INDEX_BANK_FROMDATE, fromDate);
	}

	public void setToDate(String toDate) {
		addToReq(GCConstants.INDEX_BANK_TODATE, toDate);
	}
}