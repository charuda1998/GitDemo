package com.globecapital.api.razorpay.generics;

import org.json.JSONObject;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PollOrderStatusResponse extends RazorPayResponse {

	@SerializedName("id")
	protected String paymentId;

	@SerializedName("entity")
	protected String entity;

	@SerializedName("amount")
    protected double amount;
	
	@SerializedName("amount_paid")
    protected double amountPaid;
	
	@SerializedName("amount_due")
    protected double amountDue;
		
	@SerializedName("currency")
    protected String currency;
	
	@SerializedName("receipt")
    protected String receipt;
	
	@SerializedName("offer_id")
    protected String offerId;

	@SerializedName("status")
    protected String status;
	
	@SerializedName("attempts")
    protected int attempts;
	
	@SerializedName("notes")
    protected JSONObject notes;
	
	@SerializedName("created_at")
    protected long createdAt;

	public String getPaymentId() {
		return paymentId;
	}

	public String getEntity() {
		return entity;
	}

	public double getAmount() {
		return amount;
	}

	public double getAmountPaid() {
		return amountPaid;
	}

	public double getAmountDue() {
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

	public int getAttempts() {
		return attempts;
	}

	public JSONObject getInternational() {
		return notes;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return "PollOrderStatusResponse -> paymentId=" + paymentId + ", entity=" + entity + ", amount=" + amount
				+ ", amountPaid=" + amountPaid + ", amountDue=" + amountDue + ", currency=" + currency + ", receipt="
				+ receipt + ", offerId=" + offerId + ", status=" + status + ", attempts=" + attempts
				+ ", notes=" + notes + ", createdAt=" + createdAt ;
	}

}
