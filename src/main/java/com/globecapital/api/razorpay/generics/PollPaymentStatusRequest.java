package com.globecapital.api.razorpay.generics;

import com.globecapital.services.exception.AppConfigNoKeyFoundException;

public class PollPaymentStatusRequest extends RazorPayRequest {

	public PollPaymentStatusRequest() throws AppConfigNoKeyFoundException {
		super();
	}

	@Override
	public String toString() {
		return "";
	}
}
