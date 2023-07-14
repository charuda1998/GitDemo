package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetCancelWithdrawalRequest extends GCApiRequest{
	public GetCancelWithdrawalRequest() throws AppConfigNoKeyFoundException {
		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setAuthCode(String authCode) {
		addToReq(GCConstants.INDEX_SESSIONID, authCode);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_BANKLIST_CLIENTCODE, clientCode);
	// }
	public void setTransactionId(String transId) {
		addToReq(GCConstants.INDEX_CANCEL_WITHDRAW_REFNO, transId);
	}
}
