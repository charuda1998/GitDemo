package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetFOCombinedPositionRows {

	@SerializedName("BUYSELL")
	protected String buySell;
	
	@SerializedName("Exchange")
	protected String exchange;

	@SerializedName("HoldingValue")
	protected String holdingValue;
	
	@SerializedName("PRODUCTTYPE")
	protected String productType;
	
	@SerializedName("Price")
	protected String price;
	
	@SerializedName("QTY")
	protected String qty;
	
	@SerializedName("SCRIPNAME")
	protected String scripName;
	
	@SerializedName("SEGMENTS")
	protected String segments;
	
	
	public String getBuySell() {
		return buySell;
	}

	public void setBuySell(String buySell) {
		this.buySell = buySell;
	}
	
	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	
	
	public String getHoldingValue() {
		return holdingValue;
	}

	public void setHoldingValue(String holdingValue) {
		this.holdingValue = holdingValue;
	}

	
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	
	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}
	
	
	public String getScripName() {
		return scripName;
	}

	public void setScripName(String scripName) {
		this.scripName = scripName;
	}
	
	
	public String getSegments() {
		return segments;
	}

	public void setSegments(String segments) {
		this.segments = segments;
	}
	
}
