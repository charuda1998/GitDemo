package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetBankWithdrawRequest extends GCApiRequest {

	public GetBankWithdrawRequest() throws AppConfigNoKeyFoundException {
		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setAuthCode(String authCode) {
		addToReq(GCConstants.INDEX_SESSIONID, authCode);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_BANK_WITHDRAW_CLIENTCODE, clientCode);
	// }

	public void setSegment(String segment) {
		addToReq(GCConstants.INDEX_BANK_WITHDRAW_SEGMENT, segment);
	}

	public void setAmount(String amount) {
		addToReq(GCConstants.INDEX_BANK_WITHDRAW_AMOUNT, amount);
	}

	public void setReferenceNo(String refNo) {
		addToReq(GCConstants.INDEX_BANK_WITHDRAW_REFNO, refNo);
	}

	public void setAccountNo(String acctNo) {
		addToReq(GCConstants.INDEX_BANK_WITHDRAW_ACCOUNTNO, acctNo);
	}
}
