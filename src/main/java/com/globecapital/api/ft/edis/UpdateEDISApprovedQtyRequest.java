package com.globecapital.api.ft.edis;

import org.json.JSONArray;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class UpdateEDISApprovedQtyRequest extends FTRequest {

	public UpdateEDISApprovedQtyRequest() throws AppConfigNoKeyFoundException {
		super();
	} 
	
	public void setScripDetails(JSONArray scripDetails) {
		this.addToData(FTConstants.SCRIP_DETAILS, scripDetails);
	}
}
