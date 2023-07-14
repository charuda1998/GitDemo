package com.globecapital.api.razorpay.generics;

import com.globecapital.config.AppConfig;
import com.globecapital.services.exception.GCException;


public class CreateOrderAPI extends RazorPayAPI<CreateOrderRequest, CreateOrderResponse> {

	public CreateOrderAPI() throws GCException {
		super(AppConfig.getValue("razorpay.api.createOrder"));

	}

}

