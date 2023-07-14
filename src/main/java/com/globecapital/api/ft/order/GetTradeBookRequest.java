package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetTradeBookRequest extends FTRequest{

	public GetTradeBookRequest() throws AppConfigNoKeyFoundException {
		super();
	}
}
