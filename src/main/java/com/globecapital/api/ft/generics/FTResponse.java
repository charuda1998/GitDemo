package com.globecapital.api.ft.generics;

import org.json.me.JSONException;

import com.globecapital.api.generics.ApiResponse;
import com.globecapital.services.exception.GCException;
import com.google.gson.annotations.SerializedName;

public class FTResponse extends ApiResponse {

	@SerializedName("ResponseStatus")
	protected Boolean responseStatus;

	@SerializedName("ResponseCode")
	protected String responseCode;

	@SerializedName("ErrorMessages")
    protected String errorMessages;
	
	public Boolean getResponseStatus() {
		return responseStatus;
	}

	public void setResponeStatus(Boolean responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getErrorCode() {
		return this.responseCode;
	}

	// TODO: Common Error Handling should be implemented here once the FT API Format
	// Issue for Errors is resolved
	
	public void checkError() throws GCException, JSONException {

		if (!responseStatus )
			throw new GCException("Invalid request",responseCode);
	
		if (!(errorMessages == null || errorMessages.equals(""))) {
			throw new GCException(errorMessages,responseCode);
		}
		
				

	}

}
