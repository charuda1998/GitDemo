package com.globecapital.api.gc.backoffice;

import com.google.gson.annotations.SerializedName;

public class GetTransactionRows {

	@SerializedName("QTY")
	protected String Qty;

	@SerializedName("Scripname")
	protected String Scripname;

	@SerializedName("Trdate")
	protected String Trdate;
	
	@SerializedName("Netrate")
	protected String Netrate;

	@SerializedName("tradeprice")
	protected String tradeprice;

	@SerializedName("INSTRUMENT")
	protected String instruments;
	
	@SerializedName("Instrument")
	protected String inst;
	
	@SerializedName("bs")
	protected String bs;

	@SerializedName("ISINCODE")
	protected String ISINCODE;
	
	public String getInst() {
		return inst;
	}
	
	public String getInstrument() {
		return instruments;
	}
	
	public String getNetrate() {
		return Netrate;
	}

	public void setInstrument(String instrument) {
		instruments = instrument;
	}
	
	public void setInst(String instrument) {
		inst = instrument;
	}
	
	public void setNetrate(String netrate) {
		Netrate = netrate;
	}

	public String getQty() {
		return Qty;
	}

	public void setQty(String qty) {
		Qty = qty;
	}

	public String getISINCODE() {
		return ISINCODE;
	}

	public void setISINCODE(String iSINCODE) {
		ISINCODE = iSINCODE;
	}

	public String getScripname() {
		return Scripname;
	}

	public void setScripname(String scripname) {
		Scripname = scripname;
	}

	public String getTrdate() {
		return Trdate;
	}

	public void setTrdate(String trdate) {
		Trdate = trdate;
	}

	public String getTradeprice() {
		return tradeprice;
	}

	public void setTradeprice(String tradeprice) {
		this.tradeprice = tradeprice;
	}

	public String getBs() {
		return bs;
	}

	public void setBs(String bs) {
		this.bs = bs;
	}

}
