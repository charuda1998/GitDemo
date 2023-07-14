package com.globecapital.api.razorpay.generics;

import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class PollPaymentStatusAPI extends RazorPayAPI<PollPaymentStatusRequest, PollPaymentStatusResponse> {

	public PollPaymentStatusAPI(String orderId) throws GCException {
		super(String.format(AppConfig.getValue("razorpay.api.paymentStatus"), orderId));
	}

}

