package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetTransactionScripRows {

	@SerializedName("Qty")
	protected String Qty;

	@SerializedName("TrxnType")
	protected String BuyOrSell;

	@SerializedName("Trxndate")
	protected String trxnDate;

	@SerializedName("Netrate")
	protected String Netrate;

	public String getQty() {
		return Qty;
	}

	public void setQty(String qty) {
		Qty = qty;
	}

	public String getBuyOrSell() {
		return BuyOrSell;
	}

	public void setBuyOrSell(String buyOrSell) {
		BuyOrSell = buyOrSell;
	}

	public String getTrxnDate() {
		return trxnDate;
	}

	public void setTrxnDate(String trxnDate) {
		this.trxnDate = trxnDate;
	}

	public String getNetrate() {
		return Netrate;
	}

	public void setNetrate(String netrate) {
		Netrate = netrate;
	}

}
