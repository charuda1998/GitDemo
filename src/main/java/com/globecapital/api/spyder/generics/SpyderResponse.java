package com.globecapital.api.spyder.generics;

import org.json.me.JSONException;

import com.globecapital.api.generics.ApiResponse;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.exception.GCException;
import com.google.gson.annotations.SerializedName;

public class SpyderResponse extends ApiResponse {
	
	@SerializedName("APIStatus")
	protected Boolean apiStatus;

	@SerializedName("ErrorMsg")
    protected String errorMsg;
	
	public Boolean getAPIStatus() {
		return apiStatus;
	}

	public void setAPIStatus(Boolean apiStatus) {
		this.apiStatus = apiStatus;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}


	// TODO: Common Error Handling should be implemented here once the FT API Format
	// Issue for Errors is resolved
	
	public void checkError() throws GCException, JSONException {

		if (!apiStatus )
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, errorMsg);

	}


}
