package com.globecapital.api.razorpay.generics;

import java.math.BigDecimal;

import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;

public class CreateOrderRequest extends RazorPayRequest {

	public CreateOrderRequest() throws AppConfigNoKeyFoundException {
		super();
	}

	public void setAmount(BigDecimal amount) throws JSONException, GCException {
		addToReq(RazorPayConstants.AMOUNT, amount);
	}

	public void setMethod(String method) throws JSONException {
		addToReq(RazorPayConstants.METHOD, method);
	}

	public void setReceipt(String receipt) throws JSONException {
		addToReq(RazorPayConstants.RECEIPT, receipt);
	}
	
	public void setCurrency(String currency) throws JSONException {
		addToReq(RazorPayConstants.CURRENCY, currency);
	}

	public void setBankAccount(JSONObject bankAccount) throws JSONException {
		addToReq(RazorPayConstants.BANK_ACCOUNT, bankAccount);
	}
	
	public void setNotes(JSONObject notes) throws JSONException {
		addToReq(RazorPayConstants.NOTES, notes);
	}
	
	@Override
	public String toString() {
		return this.reqObj.toString();
	}
}
