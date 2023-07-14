package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetEquityTrxnScripRequest extends GCApiRequest {

	public GetEquityTrxnScripRequest() throws AppConfigNoKeyFoundException {
		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_EQ_TRXN_CLIENTCODE, clientCode);
	// }

	public void setScripName(String scripName) {
		addToReq(GCConstants.INDEX_EQ_TRXN_SCODE, scripName);
	}

	public void setMaxSlot(String maxSlot) {
		addToReq(GCConstants.INDEX_EQ_TRXN_MAXSLOT, maxSlot);
	}

}