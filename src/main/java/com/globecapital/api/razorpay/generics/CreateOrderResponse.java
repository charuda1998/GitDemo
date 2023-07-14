package com.globecapital.api.razorpay.generics;

import java.util.ArrayList;

import org.json.JSONArray;

import com.google.gson.annotations.SerializedName;

public class CreateOrderResponse extends RazorPayResponse {

	@SerializedName("id")
	protected String id;

	@SerializedName("entity")
	protected String entity;

	@SerializedName("amount")
    protected long amount;
	
	@SerializedName("amount_paid")
    protected long amountPaid;
	
	@SerializedName("amount_due")
    protected long amountDue;
	
	@SerializedName("currency")
    protected String currency;
	
	@SerializedName("receipt")
    protected String receipt;
	
	@SerializedName("offer_id")
    protected String offerId;
	
	@SerializedName("status")
    protected String status;
	
	@SerializedName("notes")
    protected JSONArray notes;
	
	@SerializedName("created_at")
    protected long createdAt;
	
	@SerializedName("error")
    protected ErrorResponseObject error;

	public String getId() {
		return id;
	}

	public String getEntity() {
		return entity;
	}

	public long getAmount() {
		return amount;
	}

	public long getAmountPaid() {
		return amountPaid;
	}

	public long getAmountDue() {
		return amountDue;
	}

	public String getCurrency() {
		return currency;
	}

	public String getReceipt() {
		return receipt;
	}

	public String getOfferId() {
		return offerId;
	}

	public String getStatus() {
		return status;
	}

	public JSONArray getNotes() {
		return notes;
	}

	public long getCreatedAt() {
		return createdAt;
	}
	
	public ErrorResponseObject getError() {
		return error;
	}


	@Override
	public String toString() {
		return "CreateOrderResponse [id=" + id + ", entity=" + entity + ", amount=" + amount + ", amountPaid="
				+ amountPaid + ", amountDue=" + amountDue + ", currency=" + currency + ", receipt=" + receipt
				+ ", offerId=" + offerId + ", status=" + status + ", notes=" + notes + ", createdAt=" + createdAt + "]";
	}

}
