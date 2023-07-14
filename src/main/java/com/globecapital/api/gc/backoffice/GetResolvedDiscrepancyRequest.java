package com.globecapital.api.gc.backoffice;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetResolvedDiscrepancyRequest extends GCApiRequest {

	public GetResolvedDiscrepancyRequest() throws AppConfigNoKeyFoundException {
		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
	}

	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_HOLDINGS_CLIENTCODE, clientCode);
	// }
	
	public void setScripCode(String sScripCode) {
		addToReq(GCConstants.INDEX_RESOLVED_DISCREPANCY_SHCODE, sScripCode);
	}
	
	public void setBuySell(String sBuySell) {
		addToReq(GCConstants.INDEX_RESOLVED_DISCREPANCY_BUYSELL, sBuySell);
	}
	
	public void setQty(String sQty) {
		addToReq(GCConstants.INDEX_RESOLVED_DISCREPANCY_QTY, sQty);
	}
	
	public void setRate(String sRate) {
		addToReq(GCConstants.INDEX_RESOLVED_DISCREPANCY_RATE, sRate);
	}
	
	public void setRemarks(String sRemarks) {
		addToReq(GCConstants.INDEX_RESOLVED_DISCREPANCY_REMARKS, sRemarks);
	}
	
	public void setTrxnDate(String sTrxnDate) {
		addToReq(GCConstants.INDEX_RESOLVED_DISCREPANCY_TRXNDATE, sTrxnDate);
	}
	
	public void setScripName(String sScripName) {
		addToReq(GCConstants.INDEX_RESOLVED_DISCREPANCY_SHNAME, sScripName);
	}
	
	public void setTrxnType(String sTrxnType) {
		addToReq(GCConstants.INDEX_RESOLVED_DISCREPANCY_TRXNTYPE, sTrxnType);
	}

}