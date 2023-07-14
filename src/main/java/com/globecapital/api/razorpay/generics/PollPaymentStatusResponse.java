package com.globecapital.api.razorpay.generics;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PollPaymentStatusResponse extends RazorPayResponse {

	@SerializedName("entity")
	protected String entity;
	
	@SerializedName("count")
    protected int count;
	
	@SerializedName("items")
    protected List<PollPaymentStatusRows> items;
	
	public String getEntity() {
		return entity;
	}

	public int getCount() {
		return count;
	}

	public List<PollPaymentStatusRows>  getItems() {
		return items;
	}

}
