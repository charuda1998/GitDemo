package com.globecapital.api.ft.order;

import static com.globecapital.api.ft.generics.FTConstants.POS_TYPE;

import org.json.me.JSONException;

import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetNetPositionRequest extends FTRequest {

	public GetNetPositionRequest() throws AppConfigNoKeyFoundException {
		super();
	}

	public void setPosType(String posType) throws JSONException {
		addToData(POS_TYPE, posType);
	}

}
