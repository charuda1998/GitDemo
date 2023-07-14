package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetHoldingsRows {
	
	@SerializedName("DISCQTY")
	protected String sDiscQty;

	@SerializedName("StockVal")
	protected String Value;

	@SerializedName("ISIN")
	protected String ISIN;
	
	@SerializedName("UNSETTQTY")
	protected String unsettledQty;
	
	@SerializedName("HoldingCurval")
	protected String marketValue;
	
	@SerializedName("PRODUCTTYPE")
	protected String PRODUCTTYPE;

	@SerializedName("Price")
	protected String price;

	@SerializedName("QTY")
	protected String Qty;

	@SerializedName("SCRIPCODE")
	protected String SCRIPCODE;

	@SerializedName("SCRIPNAME")
	protected String SCRIPNAME;

	@SerializedName("LTP")
	protected String ltp;
	
	@SerializedName("MTFQTY")
	protected String mtfQty;
	
	public String getMTFQty() {
		return mtfQty;
	}

	public void setMTFQty(String mtfQty) {
		this.mtfQty = mtfQty;
	}
	
	public String getDiscQty() {
		return sDiscQty;
	}

	public void setDiscQty(String sDiscQty) {
		this.sDiscQty = sDiscQty;
	}
	
	public String getUnsettledQty() {
		return unsettledQty;
	}

	public void setUnsettledQty(String unsettledQty) {
		this.unsettledQty = unsettledQty;
	}

	public String getMarketValue() {
		return marketValue;
	}

	public void setMarketValue(String marketValue) {
		this.marketValue = marketValue;
	}

	public String getLtp() {
		return ltp;
	}

	public void setLtp(String ltp) {
		this.ltp = ltp;
	}

	public String getISIN() {
		return ISIN;
	}

	public void setISIN(String iSIN) {
		ISIN = iSIN;
	}

	public String getPRODUCTTYPE() {
		return PRODUCTTYPE;
	}

	public void setPRODUCTTYPE(String pRODUCTTYPE) {
		PRODUCTTYPE = pRODUCTTYPE;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getQty() {
		return Qty;
	}

	public void setQty(String qty) {
		Qty = qty;
	}

	public String getSCRIPCODE() {
		return SCRIPCODE;
	}

	public void setSCRIPCODE(String sCRIPCODE) {
		SCRIPCODE = sCRIPCODE;
	}

	public String getSCRIPNAME() {
		return SCRIPNAME;
	}

	public void setSCRIPNAME(String sCRIPNAME) {
		SCRIPNAME = sCRIPNAME;
	}

	public String toString() {
		return "scrip code : " + this.SCRIPCODE;

	}

}
