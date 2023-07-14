package com.globecapital.api.gc.generics;

import com.globecapital.api.generics.Api.RESP_TYPE;
import com.globecapital.api.generics.ApiRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GCApiRequest_v1 extends ApiRequest {

	public GCApiRequest_v1() throws AppConfigNoKeyFoundException {
		this.respType = RESP_TYPE.JSON;
		this.apiVendorName=GCConstants.VENDOR_NAME;
	}
}
