package com.globecapital.api.razorpay.generics;

import com.google.gson.annotations.SerializedName;

public class PollPaymentStatusRows {
	
	@SerializedName("id")
	protected String id;
	
	@SerializedName("entity")
	protected String entity;
	
	@SerializedName("amount")
	protected double amount;
	
	@SerializedName("currency")
	protected String currency;
	
	@SerializedName("status")
	protected String status;
	
	@SerializedName("order_id")
	protected String orderId;
	
	@SerializedName("invoice_id")
	protected String invoiceId;
	
	@SerializedName("international")
	protected boolean international;
	
	@SerializedName("amount_refunded")
	protected double amountRefunded;
	
	@SerializedName("method")
	protected String method;
	
	@SerializedName("refund_status")
	protected String refundStatus;
	
	@SerializedName("captured")
	protected boolean captured;
	
	@SerializedName("description")
	protected String description;
	
	@SerializedName("card_id")
	protected String cardId;

	@SerializedName("bank")
	protected String bank;
	
	@SerializedName("wallet")
	protected String wallet;
	
	@SerializedName("vpa")
	protected String vpa;
	
	@SerializedName("email")
	protected String email;
	
	@SerializedName("contact")
	protected String contact;
	
	@SerializedName("created_at")
	protected long createdAt;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	
	public boolean getInternational() {
		return international;
	}
	public void setInternational(boolean international) {
		this.international = international;
	}
	
	public double getAmountRefunded() {
		return amountRefunded;
	}
	public void setAmountRefunded(double amountRefunded) {
		this.amountRefunded = amountRefunded;
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	
	public boolean getCaptured() {
		return captured;
	}
	public void setCaptured(boolean captured) {
		this.captured = captured;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	
	public String getWallet() {
		return wallet;
	}
	public void setWallet(String wallet) {
		this.wallet = wallet;
	}
	
	public String getVpa() {
		return vpa;
	}
	public void setVpa(String vpa) {
		this.vpa = vpa;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	
	public long getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}
}