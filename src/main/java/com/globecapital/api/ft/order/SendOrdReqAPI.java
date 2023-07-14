package com.globecapital.api.ft.order;

import com.globecapital.api.ft.generics.FTApi;
import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class SendOrdReqAPI extends FTApi<SendOrdRequest, SendOrdReqResponse>{

	public SendOrdReqAPI() throws GCException {
		super(AppConfig.getValue("moon.api.sendOrderRequest"));
	}
}
