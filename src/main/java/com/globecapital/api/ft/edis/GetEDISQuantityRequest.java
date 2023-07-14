package com.globecapital.api.ft.edis;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetEDISQuantityRequest extends FTRequest {

	public GetEDISQuantityRequest() throws AppConfigNoKeyFoundException {
		super();
	} 
	
	public void setMktSegId(String mktSegId) {
		this.addToData(FTConstants.MKT_SEGID, mktSegId);
	}
	
	public void setToken(String token) {
		this.addToData(FTConstants.TOKEN, token);
	}
}
