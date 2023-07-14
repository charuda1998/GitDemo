package com.globecapital.api.razorpay.generics;

import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class PollOrderStatusAPI extends RazorPayAPI<PollOrderStatusRequest, PollOrderStatusResponse> {

	public PollOrderStatusAPI(String orderId) throws GCException {
		super(String.format(AppConfig.getValue("razorpay.api.orderStatus"), orderId));
	}

}

