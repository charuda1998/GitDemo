package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class GetOrderBookRequest extends FTRequest{

	public GetOrderBookRequest() throws AppConfigNoKeyFoundException {
		super();
	}
	
}
