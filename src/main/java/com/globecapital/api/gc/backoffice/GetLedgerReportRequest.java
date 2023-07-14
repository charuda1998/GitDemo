package com.globecapital.api.gc.backoffice;

import org.json.JSONException;

import com.globecapital.api.gc.generics.GCApiRequest;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.services.exception.GCException;

public class GetLedgerReportRequest extends GCApiRequest {

	public GetLedgerReportRequest() throws JSONException, GCException {
		super();
		this.reqType=REQ_TYPE.PIP_SEPARTED;
		this.respType = RESP_TYPE.JSON;
		
	}

	public void setToken(String token) {
		addToReq(GCConstants.INDEX_SESSIONID, token);
	}

	// public void setClientCode(String clientCode) {
	// 	addToReq(GCConstants.INDEX_LEDGER_CLIENTCODE, clientCode);
	// }

	public void setYear(String year) {
		addToReq(GCConstants.INDEX_LEDGER_YEAR, year);
	}

	public void setSegment(String segment) {
		addToReq(GCConstants.INDEX_LEDGER_SEGMENT, segment);
	}

	public void setFromDate(String fromDate) {
		addToReq(GCConstants.INDEX_LEDGER_FROM_DATE, fromDate);
	}

	public void setToDate(String toDate) {
		addToReq(GCConstants.INDEX_LEDGER_TO_DATE, toDate);
	}
	
	public void setEntryType(String entryType) {
		addToReq(GCConstants.INDEX_LEDGER_ENTRYTYPE,entryType);
	}

}